package DemoQA;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Test test = new Test();
		test.TestJava();
	}
	
	public void TestJava()
	{

String str = "I   am  writing  a   code";
String result = "";

for (int i = 0; i < str.length(); i++) {
    if (str.charAt(i) != ' ') {
        result = result + str.charAt(i);
    }
}
	System.out.println(result);
	}
}
