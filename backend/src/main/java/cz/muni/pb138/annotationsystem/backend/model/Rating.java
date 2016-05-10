package cz.muni.pb138.annotationsystem.backend.model;

/**
 *
 * @author Josef Pavelec, Faculty of Informatics, Masaryk University
 */
public enum Rating {
    
    POSITIVE("+"), 
    NEGATIVE("-"),
    NONSENSE("/");
    
    private String symbol;
    
    private Rating(String value) {
        this.symbol = value;
    }
    
    public String getSymbol() {
        return symbol;
    }

    // DELETE
    public static Rating getRating(String symbol) {
        switch (symbol) {
            case "+": return Rating.POSITIVE;
            case "-": return Rating.NEGATIVE;
            case "/": return Rating.NONSENSE;
        }
        return null;
    }
}
