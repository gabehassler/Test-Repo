package dr.app.beast;
public class BeastMPI {
private static final String msg = "Unable to load mpiJava or MPJ";
//    private static Method getMPIMethod(String className, String methodName) {
//        Method method = null;
//        try {
//            Class clazz = Class.forName(className);
//            method = clazz.getMethod(methodName, Class.class);
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//            throw new RuntimeException("Libraries MPJ or mpiJava do not appear in the classpath");
//        }
//        return method;
//    }
public static void Init(String[] args) {
try {
mpi.MPI.Init(args);
} catch (NoClassDefFoundError e) {
throw new RuntimeException(msg);
}
}
public static void Finalize() {
try {
mpi.MPI.Finalize();
} catch (NoClassDefFoundError e) {
throw new RuntimeException(msg);
}
}
public static class COMM_WORLD {
public static int Rank() {
int rtnValue = -1;
try {
rtnValue = mpi.MPI.COMM_WORLD.Rank();
} catch (NoClassDefFoundError e) {
throw new RuntimeException(msg);
}
return rtnValue;
}
}
}
