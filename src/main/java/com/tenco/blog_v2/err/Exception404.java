
public class Exception404 extends RuntimeException {

    // throw new Exception400("야 너 잘못 던졌어"); <-- 사용하는 시점에 호출 모습
    public Exception404(String msg){
        super(msg);
    }

}