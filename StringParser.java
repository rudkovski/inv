package crutches;

public class StringParser {
	private String s;
	
	public StringParser(String ps)
	{
		this.s=ps;
	}
	
	public StringParser()
	{
		
	}
	
	public String parseCut(String sep)
	{
		String ret="";
		int idx=s.indexOf(sep);
		ret=s.substring(0, idx);
		s=s.substring(idx+1);		
		return ret;
	}
	
	public void setValue(String p) {this.s=p;}
	public String getValue() {return this.s;}
	
	
}
