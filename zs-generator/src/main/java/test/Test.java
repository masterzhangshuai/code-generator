package test;

public class Test {

	public static void main(String[] args) {
//		System.out.println("31123".split(",")[0]);
//		System.out.println("20160107_130".matches("^([\\d]{8}_[\\d]{2,3}[,]{0,1})+$"));
//		System.out.println("1qqqq11q".matches("^(([\\d]{1,}[a-zA-Z]{1,})|([a-zA-Z]{1,}[\\d]{1,}))+$"));
		
//		System.out.println("1.11.1111".split("\\.")[0]);
		
		for(int i=1 ;i<128;i++){
			String str="union all select count(1) sexCount from marry_user.user_vcard_"+i+" where sex='female'";
			System.out.println(str);
		}
//		System.out.println();
//		for(int i=50 ;i<100;i++){
//			System.out.print(800080000+i+" ");
//		}
	}
}
