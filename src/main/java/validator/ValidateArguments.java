package validator;

public class ValidateArguments {

    public void validateAuguments(String args[]){
        String proxyPortErrMsg = validatePort(args[1]);
        if (proxyPortErrMsg != null){
            System.out.println(proxyPortErrMsg);
            System.exit(1);
        }
    }

    private String validatePort(String port) {
        String result = null;
        try {
            Integer.parseInt(port);
        } catch (NumberFormatException ne) {
            result = "Proxy Port must be number.";
        }
        return result;
    }
}
