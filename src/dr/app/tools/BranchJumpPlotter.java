package dr.app.tools;

import dr.evolution.io.Importer;
import dr.evolution.io.NexusImporter;
import dr.evolution.tree.FlexibleNode;
import dr.evolution.tree.FlexibleTree;
import dr.evolution.tree.Tree;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;

public class BranchJumpPlotter {

    private NexusImporter treesIn;
    private NexusExporter treesOut;
    private String traitName;

    public BranchJumpPlotter(NexusImporter treesIn, NexusExporter treesOut, String traitName){
        this.treesIn = treesIn;
        this.treesOut = treesOut;
        this.traitName = traitName;
    }

    private FlexibleTree rewireTree(Tree tree, boolean verbose){
        int totalJumps = 0;
        FlexibleTree outTree = new FlexibleTree(tree, true);
        for(int nodeNo = 0; nodeNo < outTree.getNodeCount(); nodeNo++){

            FlexibleNode node = (FlexibleNode)outTree.getNode(nodeNo);
            String finalHost = (String)node.getAttribute(traitName);
            node.setAttribute(traitName,finalHost.replaceAll("\"",""));
            Object[] jumps = readCJH(node);
            if(verbose){
                System.out.print("Node "+nodeNo+": ");
            }

            if(jumps != null){

                FlexibleNode needsNewParent = node;
                Double height = tree.getNodeHeight(node);

                for (int i = jumps.length-1; i>=0; i--) {
                    totalJumps++;
                    Object[] jump = (Object[])jumps[i];
                    if(i<jumps.length-1 && (Double)jump[1] <= height){
                        throw new RuntimeException("Jumps do not appear to be in descending order of height");
                    }
                    height = (Double)jump[1];

                    if(!needsNewParent.getAttribute(traitName).equals(jump[3])){
                        throw new RuntimeException("Destination traits do not match");
                    }
                    FlexibleNode parent = (FlexibleNode)outTree.getParent(needsNewParent);
                    outTree.beginTreeEdit();
                    outTree.removeChild(parent, needsNewParent);



                    needsNewParent.setLength(height-needsNewParent.getHeight());
                    FlexibleNode jumpNode = new FlexibleNode();
                    jumpNode.setHeight(height);
                    jumpNode.setLength(parent.getHeight() - height);
                    jumpNode.setAttribute(traitName,jump[2]);
                    outTree.addChild(parent, jumpNode);
                    outTree.addChild(jumpNode, needsNewParent);
                    outTree.endTreeEdit();
                    needsNewParent = jumpNode;
                }
            }
            if(verbose){
                if(jumps==null){
                    System.out.println(0+" ("+totalJumps+")");
                } else {
                    System.out.println(jumps.length+" ("+totalJumps+")");
                }
            }
        }

        outTree = new FlexibleTree((FlexibleNode)outTree.getRoot());
        if(verbose){
            System.out.println("Total jumps: "+totalJumps);
            int[] childCounts = new int[3];
            for(int i=0; i<outTree.getNodeCount(); i++){
                childCounts[outTree.getChildCount(outTree.getNode(i))]++;
            }
            for(int i=0; i<3; i++){
                System.out.println(childCounts[i]+" nodes have "+i+" children");
            }
        }
        return outTree;
    }

    private Object[] readCJH(FlexibleNode node){
        if(node.getAttribute("history_all")!=null){
            HashSet<String[]> out = new HashSet<String[]>();
            Object[] cjh = (Object[])node.getAttribute("history_all");
            return cjh;
        } else {
            return null;
        }
    }

    private void translateTreeFile(){
        try{
            ArrayList<Tree> trees = new ArrayList<Tree>();
            int count = 1;
            while(treesIn.hasTree()){
                System.out.println("Doing tree "+count);

                trees.add(rewireTree(treesIn.importNextTree(),true));
                count++;
                System.out.println();
            }
            Tree[] treeArray = trees.toArray(new Tree[trees.size()]);
            treesOut.exportTrees(treeArray);
        } catch(IOException e){
            System.out.println("Problem reading file ("+e.toString()+")");
        } catch(Importer.ImportException e){
            System.out.println("Problem importing trees ("+e.toString()+")");
        }
    }

    public static void main(String[] args){
        try{
            String traitName = args[0];
            String inputFile = args[1];
            String outputFile = args[2];
            NexusImporter importer = new NexusImporter(new FileReader(inputFile));
            NexusExporter exporter = new NexusExporter(new PrintStream(outputFile));
            BranchJumpPlotter plotter = new BranchJumpPlotter(importer,exporter,traitName);
            plotter.translateTreeFile();
        } catch(FileNotFoundException e){
            System.out.println("File not found");
        }

    }



}
