package dr.xml;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
public class UserInput {
	public static AbstractXMLObjectParser STRING_PARSER = new AbstractXMLObjectParser() {
		public String getParserName() { return "string"; }
		public Object parseXMLObject(XMLObject xo) throws XMLParseException {
			if (xo.hasAttribute("prompt")) {
				String prompt = xo.getStringAttribute("prompt");	
				System.out.print(prompt+": ");
				System.out.flush();			
				return input.readString();
			} else {
				return xo.getChild(String.class);
			}
		}
		//************************************************************************
		// AbstractXMLObjectParser implementation
		//************************************************************************
		public String getParserDescription() {
			return "returns a String. If a prompt attribute exists then the user is prompted for input, otherwise the character contents of the element are returned.";
		}
		public Class getReturnType() { return String.class; }
		public XMLSyntaxRule[] getSyntaxRules() { return rules; }
		private XMLSyntaxRule[] rules = new XMLSyntaxRule[] {  
			new XORRule(
				new StringAttributeRule(
					"prompt", 
					"A message displayed to the user when entering a value for this string", 
					"Enter the name of a dinosaur:"),
				new ElementRule(String.class))
		};
	};
	public static AbstractXMLObjectParser DOUBLE_PARSER = new AbstractXMLObjectParser() {
		public String getParserName() { return "double"; }
		public Object parseXMLObject(XMLObject xo) throws XMLParseException {
			if (xo.hasAttribute("prompt")) {
				String prompt = xo.getStringAttribute("prompt");	
				System.out.print(prompt+": ");
				System.out.flush();			
				return new Double(input.readDouble());
			} else {
				return xo.getChild(Double.class);
			}
		}
		//************************************************************************
		// AbstractXMLObjectParser implementation
		//************************************************************************
		public String getParserDescription() {
			return "returns a Double. If a prompt attribute exists then the user is prompted for input, otherwise the character contents of the element are returned as a Double.";
		}
		public Class getReturnType() { return Double.class; }
		public XMLSyntaxRule[] getSyntaxRules() { return rules; }
		private XMLSyntaxRule[] rules = new XMLSyntaxRule[] {  
			new XORRule(
				new StringAttributeRule(
					"prompt", 
					"A message displayed to the user when entering a value for this double",
					"Enter the length of a piece of string (in metres):"),
				new ElementRule(Double.class))
		};
	};
	public static AbstractXMLObjectParser INTEGER_PARSER = new AbstractXMLObjectParser() {
		public String getParserName() { return "integer"; }
		public Object parseXMLObject(XMLObject xo) throws XMLParseException {
			if (xo.hasAttribute("prompt")) {
				String prompt = xo.getStringAttribute("prompt");	
				System.out.print(prompt+": ");
				System.out.flush();			
				return new Integer(input.readInteger());
			} else {
				return xo.getChild(Integer.class);
			}
		}
		//************************************************************************
		// AbstractXMLObjectParser implementation
		//************************************************************************
		public String getParserDescription() {
			return "returns an Integer. If a prompt attribute exists then the user is prompted for input, otherwise the character contents of the element are returned as an Integer.";
		}
		public Class getReturnType() { return Integer.class; }
		public XMLSyntaxRule[] getSyntaxRules() { return rules; }
		private XMLSyntaxRule[] rules = new XMLSyntaxRule[] {  
			new XORRule(
				new StringAttributeRule(
					"prompt", 
					"A message displayed to the user when entering a value for this integer",
					"Enter the number of categories:"),
				new ElementRule(Integer.class))
		};
	};
	static KeyboardInput input = new KeyboardInput();
}
class KeyboardInput
{
  private final BufferedReader in =
    new BufferedReader(new InputStreamReader(System.in));
  public final synchronized int readInteger()
  {
    String input = "";
    int value = 0;
    try
    {
      input = in.readLine();
    }
    catch (IOException e)
    {}
    if (input != null)
    {
      try
      {
        value = Integer.parseInt(input);
      }
      catch (NumberFormatException e)
      {}
    }
    return value;
  }
  public final synchronized long readLong()
  {
    String input = "";
    long value = 0L;
    try
    {
      input = in.readLine();
    }
    catch (IOException e)
    {}
    if (input != null)
    {
      try
      {
        value = Long.parseLong(input);
      }
      catch (NumberFormatException e)
      {}
    }
    return value;
  }
  public final synchronized double readDouble()
  {
    String input = "";
    double value = 0.0D;
    try
    {
      input = in.readLine();
    }
    catch (IOException e)
    {}
    if (input != null)
    {
      try
      {
        value = Double.parseDouble(input);
      }
      catch (NumberFormatException e)
      {}
    }
    return value;
  }
  public final synchronized float readFloat()
  {
    String input = "";
    float value = 0.0F;
    try
    {
      input = in.readLine();
    }
    catch (IOException e)
    {}
    if (input != null)
    {
      try
      {
        value = Float.parseFloat(input);
      }
      catch (NumberFormatException e)
      {}
    }
    return value;
  }
  public final synchronized char readCharacter()
  {
    char c = ' ';
    try
    {
      c = (char) in.read();
    }
    catch (IOException e)
    {}
    return c;
  }
  public final synchronized String readString()
  {
    String s = "";
    try
    {
      s = in.readLine();
    }
    catch (IOException e)
    {}
    if (s == null)
    {
      s = "";
    }
    return s;
  }
}
