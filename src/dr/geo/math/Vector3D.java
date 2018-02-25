package dr.geo.math;
public final class Vector3D {
protected double x;
protected double y;
protected double z;
public static final Vector3D ORIGIN = new Vector3D(0.0, 0.0, 0.0);
public Vector3D(double x, double y, double z) {
this.x = x;
this.y = y;
this.z = z;
}
public Vector3D(Vector3D a) {
this.x = a.x;
this.y = a.y;
this.z = a.z;
}
public Vector3D add(Vector3D a) {
return (new Vector3D(x + a.x, y + a.y, z + a.z));
}
public Vector3D addU(Vector3D a) {
x += a.x;
y += a.y;
z += a.z;
return (this);
}
public Vector3D cross(Vector3D a) {
return (new Vector3D(y * a.z - z * a.y, z * a.x - x * a.z, x * a.y - y * a.x));
}
public double dot(Vector3D a) {
return (x * a.x + y * a.y + z * a.z);
}
public void negate() {
x = -x;
y = -y;
z = -z;
}
public boolean equals(Object a) {
if (!(a instanceof Vector3D))
return (false);
return ((x == ((Vector3D) a).x) && (x == ((Vector3D) a).y) && (x == ((Vector3D) a).z));
}
public Vector3D mirror(Vector3D a) {
return (this.add(a.mul(-2.0 * a.dot(this))));
}
public double getX() { return x; }
public double getY() { return y; }
public double getZ() { return z; }
public double getCoordinate(int index) {
switch (index) {
case 0: return x;
case 1: return y;
case 2: return z;
default: throw new IllegalArgumentException("illegal coordinates:" + index);
}
}
public double modulus() {
return (Math.sqrt(x * x + y * y + z * z));
}
public double mod2() {
return (x * x + y * y + z * z);
}
public Vector3D mul(double a) {
return (new Vector3D(x * a, y * a, z * a));
}
public Vector3D mulU(double a) {
x *= a;
y *= a;
z *= a;
return (this);
}
public Vector3D sub(Vector3D a) {
return (new Vector3D(x - a.x, y - a.y, z - a.z));
}
public Vector3D subU(Vector3D a) {
x -= a.x;
y -= a.y;
z -= a.z;
return (this);
}
public Vector3D normalized() {
return (this.mul(1.0 / this.modulus()));
}
public Vector3D normalize() {
double im = 1.0 / this.modulus();
this.x *= im;
this.y *= im;
this.z *= im;
return (this);
}
public String toString() {
return ("Vector3D[" + x + "," + y + "," + z + "]");
}
}