package org.webluminous.mail;

/**
 * This Util Class where we can per form the operation of String
 *
 * @author Manikandan on 11/8/2016.
 * @version 1.0
 * @since 11/2016
 */
public class StringUtils {

  /**
   * @param s Inout string
   *
   * @return returns true if its not null else false
   */
  public static boolean isNotNull(String s) {
    return s != null && !"".equals(s);
  }
}
