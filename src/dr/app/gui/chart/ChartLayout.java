package dr.app.gui.chart;
import java.awt.*;
public class ChartLayout implements LayoutManager2 {
int hgap;
int vgap;
Component title = null;
Component xLabel = null;
Component yLabel = null;
Component chart = null;
public ChartLayout() {
this(0, 0);
}
public ChartLayout(int hgap, int vgap) {
this.hgap = hgap;
this.vgap = vgap;
}
public int getHgap() {
return hgap;
}
public void setHgap(int hgap) {
this.hgap = hgap;
}
public int getVgap() {
return vgap;
}
public void setVgap(int vgap) {
this.vgap = vgap;
}
public void addLayoutComponent(Component comp, Object constraints) {
synchronized (comp.getTreeLock()) {
if ((constraints == null) || (constraints instanceof String)) {
addLayoutComponent((String) constraints, comp);
} else {
throw new IllegalArgumentException("cannot add to layout: constraint must be a string (or null)");
}
}
}
public void addLayoutComponent(String name, Component comp) {
synchronized (comp.getTreeLock()) {
if (name == null) {
name = "Chart";
}
if ("Chart".equals(name) || "Table".equals(name)) {
chart = comp;
} else if ("Title".equals(name)) {
title = comp;
} else if ("XLabel".equals(name)) {
xLabel = comp;
} else if ("YLabel".equals(name)) {
yLabel = comp;
} else {
throw new IllegalArgumentException("cannot add to layout: unknown constraint: " + name);
}
}
}
public void removeLayoutComponent(Component comp) {
synchronized (comp.getTreeLock()) {
if (comp == chart) {
chart = null;
} else if (comp == title) {
title = null;
} else if (comp == xLabel) {
xLabel = null;
} else if (comp == yLabel) {
yLabel = null;
}
}
}
public Dimension minimumLayoutSize(Container target) {
synchronized (target.getTreeLock()) {
Dimension dim = new Dimension(0, 0);
if ((chart != null) && chart.isVisible()) {
Dimension d = chart.getMinimumSize();
dim.width = d.width;
dim.height = d.height;
}
if ((xLabel != null) && xLabel.isVisible()) {
Dimension d = xLabel.getMinimumSize();
dim.width = Math.max(d.width, dim.width);
dim.height += d.height + vgap;
}
if ((yLabel != null) && yLabel.isVisible()) {
Dimension d = yLabel.getMinimumSize();
dim.width += d.width + hgap;
dim.height = Math.max(d.height, dim.height);
}
if ((title != null) && title.isVisible()) {
Dimension d = title.getMinimumSize();
dim.width = Math.max(d.width, dim.width);
dim.height += d.height + vgap;
}
Insets insets = target.getInsets();
dim.width += insets.left + insets.right;
dim.height += insets.top + insets.bottom;
return dim;
}
}
public Dimension preferredLayoutSize(Container target) {
synchronized (target.getTreeLock()) {
Dimension dim = new Dimension(0, 0);
if ((chart != null) && chart.isVisible()) {
Dimension d = chart.getPreferredSize();
dim.width = d.width;
dim.height = d.height;
}
if ((xLabel != null) && xLabel.isVisible()) {
Dimension d = xLabel.getPreferredSize();
dim.width = Math.max(d.width, dim.width);
dim.height += d.height + vgap;
}
if ((yLabel != null) && yLabel.isVisible()) {
Dimension d = yLabel.getPreferredSize();
dim.width += d.width + hgap;
dim.height = Math.max(d.height, dim.height);
}
if ((title != null) && title.isVisible()) {
Dimension d = title.getPreferredSize();
dim.width = Math.max(d.width, dim.width);
dim.height += d.height + vgap;
}
Insets insets = target.getInsets();
dim.width += insets.left + insets.right;
dim.height += insets.top + insets.bottom;
return dim;
}
}
public Dimension maximumLayoutSize(Container target) {
return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
}
public float getLayoutAlignmentX(Container parent) {
return 0.5f;
}
public float getLayoutAlignmentY(Container parent) {
return 0.5f;
}
public void invalidateLayout(Container target) {
}
public void layoutContainer(Container target) {
synchronized (target.getTreeLock()) {
Insets insets = target.getInsets();
Dimension d;
d = target.getSize();
int top = insets.top;
int bottom = d.height - insets.bottom;
int left = insets.left;
int right = d.width - insets.right;
// defines the centre box.
int top1 = top;
int bottom1 = bottom;
int left1 = left;
if ((xLabel != null) && xLabel.isVisible()) {
d = xLabel.getPreferredSize();
bottom1 -= d.height + vgap;
}
if ((yLabel != null) && yLabel.isVisible()) {
d = yLabel.getPreferredSize();
left1 += d.width + hgap;
}
if ((title != null) && title.isVisible()) {
d = title.getPreferredSize();
top1 += d.height + vgap;
}
if ((xLabel != null) && xLabel.isVisible())
xLabel.setBounds(left1, bottom1, right - left1, bottom - bottom1);
if ((yLabel != null) && yLabel.isVisible())
yLabel.setBounds(left, top1, left1 - left, bottom1 - top1);
if ((title != null) && title.isVisible())
title.setBounds(left, top, right - left, top1 - top);
if ((chart != null) && chart.isVisible())
chart.setBounds(left1, top1, right - left1, bottom1 - top1);
}
}
public String toString() {
return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + "]";
}
}
