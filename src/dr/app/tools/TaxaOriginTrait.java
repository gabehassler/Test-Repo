
package dr.app.tools;

import dr.evolution.io.Importer;
import dr.evolution.io.NexusImporter;
import dr.evolution.tree.FlexibleNode;
import dr.evolution.tree.FlexibleTree;
import dr.evolution.tree.NodeRef;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class TaxaOriginTrait {

    private FlexibleTree[] trees;
    private String[] taxaNames;
    private String traitName;
    private String attributeName;
    private String fileNameRoot;

    private TaxaOriginTrait(String[] taxa, FlexibleTree[] trees,
                            String attributeName, String fileNameRoot){
        this.taxaNames = taxa;
        this.trees = trees;
        this.attributeName = attributeName;
        this.fileNameRoot = fileNameRoot;

        traitName = getTraitName();
    }


    private FlexibleNode findCommonAncestor(FlexibleTree tree, FlexibleNode[] nodes){
        HashSet<FlexibleNode> doneNodes = new HashSet<FlexibleNode>();
        FlexibleNode currentParent = nodes[0];
        for(FlexibleNode node: nodes){
            doneNodes.add(node);
            boolean ancestorOfAllDoneNodes = false;
            currentParent = node;
            if(getTipSet(tree, currentParent).containsAll(doneNodes)){
                ancestorOfAllDoneNodes=true;
            }
            while(!ancestorOfAllDoneNodes){
                currentParent = (FlexibleNode)tree.getParent(currentParent);
                if(getTipSet(tree, currentParent).containsAll(doneNodes)){
                    ancestorOfAllDoneNodes=true;
                }
            }
        }
        return currentParent;
    }

    private String getTraitName(){
        FlexibleTree tree = trees[0];

        String traitName = null;

        for(int i=0; i<tree.getExternalNodeCount(); i++){
            NodeRef node = tree.getExternalNode(i);

            for(String taxaName : taxaNames){
                if(tree.getNodeTaxon(node).getId().equals(taxaName)){
                    String attributeValue = (String)tree.getNodeAttribute(node, attributeName);

                    if(traitName!=null && !traitName.equals(attributeValue)){
                        throw new RuntimeException("Not all taxa given have the same trait value");
                    } else {
                        traitName = attributeValue;
                    }

                }

            }

        }

        return  traitName;

    }



    private boolean branchNode(FlexibleTree tree, FlexibleNode node){
        return tree.getChildCount(node)==2;
    }



    private FlexibleNode[] getTipsOfInterest(FlexibleTree tree){
        HashSet<FlexibleNode> tempSet = new HashSet<FlexibleNode>();

        for(String taxonName: taxaNames){
            for(int i=0; i<tree.getExternalNodeCount(); i++){
                if(tree.getNodeTaxon(tree.getExternalNode(i)).toString().equals(taxonName)){
                    tempSet.add((FlexibleNode)tree.getExternalNode(i));
                }
            }
        }
        return tempSet.toArray(new FlexibleNode[tempSet.size()]);
    }

    private HashMap<String,String> getIncomingJumpOrigins(FlexibleTree tree){
        HashMap<String,String> out = new HashMap<String, String>();

        FlexibleNode[] tips = getTipsOfInterest(tree);
        FlexibleNode mrca = findCommonAncestor(tree, tips);
        HashSet<FlexibleNode> taxaSet = new HashSet<FlexibleNode>(Arrays.asList(tips));
        HashSet<FlexibleNode> tipSet = getTipSet(tree, mrca);
        if(!taxaSet.containsAll(tipSet)){
            System.out.println("WARNING: mixed traits in a clade");
        }
        if(!mrca.getAttribute(attributeName).equals(traitName)){
            out.put(traitName,"Multiple");
            System.out.println("Multiple origin found.");
        } else {
            boolean sameTrait = true;
            FlexibleNode currentParent = mrca;
            while(sameTrait){
                currentParent = (FlexibleNode)tree.getParent(currentParent);
                if(currentParent==null){
                    out.put(traitName,"root");
                    break;
                } else {
                    String parentTrait = (String)currentParent.getAttribute(attributeName);
                    if(!parentTrait.equals(traitName)){
                        sameTrait = false;
                        out.put(traitName,parentTrait);
                    }
                }
            }


        }
        return out;
    }


    private HashSet<FlexibleNode> getTipSet(FlexibleTree tree, FlexibleNode node){
        HashSet<FlexibleNode> out = new HashSet<FlexibleNode>();
        if(tree.isExternal(node)){
            out.add(node);
            return out;
        } else {
            for(int i=0; i<tree.getChildCount(node); i++){
                out.addAll(getTipSet(tree, (FlexibleNode)tree.getChild(node, i)));
            }
        }
        return out;
    }

    private void tabulateOrigins(){

        HashMap<String,Integer> countsMap = new HashMap<String, Integer>();

        int count = 0;
        for(FlexibleTree currentTree: trees){
            if(count % 1 == 0){
                System.out.println("Doing tree "+count);
            }
            HashMap<String,String> results = getIncomingJumpOrigins(currentTree);
            for(String key: results.keySet()){
                int oldCount = countsMap.containsKey(results.get(key)) ? countsMap.get(results.get(key)) : 0;
                countsMap.put(results.get(key), oldCount+1);
            }
            count++;
        }

        try{

                BufferedWriter outWriter = new BufferedWriter(new FileWriter(fileNameRoot+".csv"));
                for(String key: countsMap.keySet()){
                    outWriter.write(key+","+countsMap.get(key)+"\n");
                }
                outWriter.flush();

        } catch(IOException e){
            System.out.println("Failed to write to file");
        }
    }



    public static void main(String[] args){
        try{
            BufferedReader taxonReader = new BufferedReader(new FileReader(args[1]));
            HashSet<String> tempTaxa = new HashSet<String>();
            String line;
            while((line = taxonReader.readLine())!=null){
                tempTaxa.add(line);
            }
            String[] taxa = tempTaxa.toArray(new String[tempTaxa.size()]);

            NexusImporter importer = new NexusImporter(new FileReader(args[2]));
            importer.setSuppressWarnings(true);
            ArrayList<FlexibleTree> tempTrees = new ArrayList<FlexibleTree>();
            int count = 0;
            while(importer.hasTree()){
                if(count % 100 == 0){
                    System.out.println("Loaded "+count+" trees");
                }
                tempTrees.add((FlexibleTree)importer.importNextTree());
                count++;
            }
            FlexibleTree[] trees = tempTrees.toArray(new FlexibleTree[tempTrees.size()]);
            TaxaOriginTrait examiner = new TaxaOriginTrait(taxa,trees,args[0],
                    args[3]);
            examiner.tabulateOrigins();
        } catch(IOException e){
            System.out.println("Failed to read files");
        } catch(Importer.ImportException e){
            System.out.println("Failed to import trees");
        }
    }


}
