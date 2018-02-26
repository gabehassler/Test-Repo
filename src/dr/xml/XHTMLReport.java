
package dr.xml;

import dr.util.XHTMLable;


public class XHTMLReport extends Report {
		
	public void createReport() {
		System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>");
		System.out.print("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" ");
		System.out.println("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
		System.out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		System.out.println("<head>");
		System.out.println("<title>");
		System.out.println(getTitle());
		System.out.println("</title>");
		System.out.println("</head>");
		System.out.println("<body>");
		
		for (int i = 0; i < objects.size(); i++) {
			if (objects.get(i) instanceof XHTMLable)
				System.out.println(((XHTMLable)objects.get(i)).toXHTML());
			else {
				System.out.print("<p>");
				System.out.print(objects.get(i).toString());
				System.out.println("</p>");
			}
		}
		System.out.println("</body>");
		System.out.println("</html>");
	}
}
