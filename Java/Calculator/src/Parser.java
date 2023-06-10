import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Parser {
	public int[] parse(String input) {
		// 요구사항 : 빈값, null이 들어올 경우 0으로 대치
		if(input.equals("") || input == null) {
			return new int[] {0};
		}
		
		// 요구사항 : 쉼표 또는 콜론을 구분자로 가지는 문자열이 input으로 들어올 경우
 		//          구분자를 기준으로 숫자 구분
		String[] s = input.split(",|:");
		
		return Stream.of(s).mapToInt(Integer::parseInt).toArray();
		
		// 커스텀 구분자 추가해야댐
	}
}
