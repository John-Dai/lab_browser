    public class BrowserException extends Exception {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        public String myMessage;
        public BrowserException(String message){
            myMessage=message;
        }
    }