package mg.hei.agrifed.agrifedapi.exception;

public class CollectivityNotFoundException extends RuntimeException {
    public CollectivityNotFoundException(String message) {
        super(message);
    }

    public CollectivityNotFoundException(long id){
        super("Collectivity n°"+id+" is not found in the federation");
    }
}
