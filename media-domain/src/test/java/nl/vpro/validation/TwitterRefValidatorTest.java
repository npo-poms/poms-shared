package nl.vpro.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.12
 */
@SuppressWarnings("deprecation")
public class TwitterRefValidatorTest {

    @Test
    public void isValid() {


        assertThat(TwitterRefValidator.PATTERN.matcher("@aaaa").matches()).isTrue();
        assertThat(TwitterRefValidator.PATTERN.matcher("@aaaa bb").matches()).isFalse();
        assertThat(TwitterRefValidator.PATTERN.matcher("@" + new String(new char[50]).replace('\0', 'a')).matches()).isTrue();
        assertThat(TwitterRefValidator.PATTERN.matcher("@" + new String(new char[51]).replace('\0', 'a')).matches()).isFalse();
        assertThat(TwitterRefValidator.PATTERN.matcher("#abcdefghijklmnopqrstuvwxyz").matches()).isTrue();
        assertThat(TwitterRefValidator.PATTERN.matcher("#pointer").matches()).isTrue();


    }
}
