package com.tsingye.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class ObjectUtils {

  /**
   * 判断对象是否为null或empty, 支持字符串、数组、集合、Optional
   *
   * @param obj 被判断的对象
   * @return 如果对象为null, 或者长度(字符串、数组、集合)为0, 或者不包含元素(Optional), 返回true
   */
  public static boolean isEmpty(Object obj) {
    if (obj == null) {
      return true;
    }

    if (obj instanceof Optional) {
      return !((Optional) obj).isPresent();
    }
    if (obj instanceof CharSequence) {
      return ((CharSequence) obj).length() == 0;
    }
    if (obj.getClass().isArray()) {
      return Array.getLength(obj) == 0;
    }
    if (obj instanceof Collection) {
      return ((Collection) obj).isEmpty();
    }
    if (obj instanceof Map) {
      return ((Map) obj).isEmpty();
    }

    // else
    return false;
  }

  public static boolean notEmpty(Object obj) {
    return !isEmpty(obj);
  }

}
