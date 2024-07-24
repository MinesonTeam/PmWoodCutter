package kz.hxncus.mc.pmwoodcutter.color.caching;

import lombok.Getter;

import java.util.Objects;

@Getter
public class LruElement {
    private final String input;

    private final String result;

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof LruElement))
            return false;
        LruElement other = (LruElement) o;
        if (!other.canEqual(this))
            return false;
        Object thisInput = getInput();
        Object otherInput = other.getInput();
        if (!Objects.equals(thisInput, otherInput))
            return false;
        Object thisResult = getResult();
        Object otherResult = other.getResult();
        return Objects.equals(thisResult, otherResult);
    }

    protected boolean canEqual(Object other) {
        return other instanceof LruElement;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object input = getInput();
        result = result * 59 + ((input == null) ? 43 : input.hashCode());
        Object $result = getResult();
        return result * 59 + (($result == null) ? 43 : $result.hashCode());
    }

    public String toString() {
        return "LruElement(input=" + getInput() + ", result=" + getResult() + ")";
    }

    public LruElement(String input, String result) {
        this.input = input;
        this.result = result;
    }
}
