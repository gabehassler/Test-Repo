package dr.evomodel.indel;
class IntMathVec implements Cloneable {
    public int[] iV;
    IntMathVec(int iLen) {
	iV = new int[iLen];
    }
    IntMathVec(IntMathVec iVec) {
	iV = iVec.iV.clone();
    }
    IntMathVec(int[] iArr) {
	iV = iArr.clone();
    }
    // I know, I should throw an exception..  Will likely happen automatically.
    private void check(IntMathVec iVec) {
	if (iVec.iV.length != iV.length) {
	    System.out.println("IntMathVec.check: Vector sizes don't match.");
	}
    }
    public boolean equals(Object iObj) {
	if (iObj instanceof IntMathVec) {
	    IntMathVec iVec = (IntMathVec)iObj;
	    if (iVec.iV.length != iV.length)
		return false;
	    for (int i=0; i<iV.length; i++) {
		if (iV[i] != iVec.iV[i])
		    return false;
	    }
	    return true;
	}
	return false;
    }
    public int hashCode() {
        int iCode = 0;
        for( int anIV : iV ) {
            iCode = (iCode * 75) + anIV;
        }
        return iCode;
    }
    public IntMathVec clone() {
        try {
            // This magically creates an object of the right type
            IntMathVec iObj = (IntMathVec) super.clone();
            iObj.iV = iV.clone();
            return iObj;
        } catch (CloneNotSupportedException e) {
            System.out.println("IntMathVec.clone: Something happened that cannot happen -- ?");
            return null;
        }
    }
    public String toString() {
	String iResult = "{";
	for (int i=0; i<iV.length; i++) {
	    if (i != 0)
		iResult += ",";
	    iResult += iV[i];
	}
	iResult += "}";
	return iResult;
    }
    public int innerProduct(IntMathVec iVec) {
	check(iVec);
	int iSum = 0;
	for (int i=0; i<iV.length; i++)
	    iSum += iVec.iV[i]*iV[i];
	return iSum;
    }
    public boolean zeroEntry() {
        for(int anIV : iV) {
            if( anIV == 0 ) {
                return true;
            }
        }
	return false;
    }
    public void assign(IntMathVec iVec) {
	iV = iVec.iV.clone();
    }
    public void add(IntMathVec iVec) {
	check(iVec);
	for (int i=0; i<iV.length; i++)
	    iV[i] += iVec.iV[i];
    }
    public void addMultiple(IntMathVec iVec, int iMultiple) {
	check(iVec);
	for (int i=0; i<iV.length; i++)
	    iV[i] += iVec.iV[i] * iMultiple;
    }
    public void subtract(IntMathVec iVec) {
	check(iVec);
	for (int i=0; i<iV.length; i++)
	    iV[i] -= iVec.iV[i];
    }
}
