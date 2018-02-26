
package dr.inference.prior;

//public class CompositePrior implements Prior {
//
//	Vector priors = null;
//	String name = null;
//
//	public CompositePrior(String name) {
//		priors = new Vector();
//		this.name = name;
//	}
//
//	public CompositePrior(String name, Prior[] priorArray) {
//
//		this.name = name;
//		priors = new Vector(priorArray.length);
//		for (int i =0; i < priorArray.length; i++) {
//			priors.add(priorArray[i]);
//		}
//	}
//
//	public double getLogPrior(dr.inference.model.Model model) {
//
//		if (priors == null) return 0.0;
//
//		double logPrior = 0.0;
//
//		for (int i = 0; i < priors.size(); i++) {
//			Prior p = (Prior)priors.elementAt(i);
//			double l = p.getLogPrior(model);
//			logPrior += l;
//		}
//
//		return logPrior;
//	}
//
//	public void addPrior(Prior p) {
//		if (p != null) priors.add(p);
//	}
//
//	public Iterator getPriorIterator() {
//		return priors.iterator();
//	}
//
//	public String getPriorName() {
//		return name;
//	}
//
//	public Element createElement(Document d) {
//		throw new RuntimeException("Not implemented!");
//	}
//
//	public String toString() {
//		StringBuffer buffer = new StringBuffer();
//		for (int i = 0; i < priors.size(); i++) {
//			Prior p = (Prior)priors.elementAt(i);
//			buffer.append(p.toString());
//		}
//		return buffer.toString();
//	}
//}
