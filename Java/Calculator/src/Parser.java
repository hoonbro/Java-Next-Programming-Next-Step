import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Parser {
	public int[] parse(String input) {
		// �䱸���� : ��, null�� ���� ��� 0���� ��ġ
		if(input.equals("") || input == null) {
			return new int[] {0};
		}
		
		// �䱸���� : ��ǥ �Ǵ� �ݷ��� �����ڷ� ������ ���ڿ��� input���� ���� ���
 		//          �����ڸ� �������� ���� ����
		String[] s = input.split(",|:");
		
		return Stream.of(s).mapToInt(Integer::parseInt).toArray();
		
		// Ŀ���� ������ �߰��ؾߴ�
	}
}
