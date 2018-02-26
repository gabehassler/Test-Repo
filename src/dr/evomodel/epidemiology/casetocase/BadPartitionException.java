package dr.evomodel.epidemiology.casetocase;
public class BadPartitionException extends RuntimeException {
    public BadPartitionException(String s){
        super(s);
    }
    public BadPartitionException(AbstractCase parentCase, AbstractCase childCase, double infectionTime){
        super("Suggesting that "+parentCase.getName()+" infected "+childCase.getName()+" at "+infectionTime+" which" +
                " is not permitted");
    }
}
