package com.github.sylphlike.framework.web.exception;

import com.github.sylphlike.framework.web.FReply;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * 断言
 * <p>  time 17:56 2021/01/29  星期五 </p>
 * <p> email 15923508369@163.com     </p>
 * @author Gopal.pan
 * @version 1.0.0
 */
public abstract class AssertUtils {

    /**
     * Assert a boolean expression, throwing an {@code IllegalStateException}
     * if the expression evaluates to {@code false}.
     * on an assertion failure.
     * @param expression a boolean expression
     * @param message the exception message to use if the assertion fails
     * @throws IllegalStateException if {@code expression} is {@code false}
     */
    public static void state(boolean expression, String message)  {
        if (!expression) {
            throw new ServiceException(FReply.FW_LOGIC_DATA_ERROR,message);
        }
    }





    /**
     * Assert a boolean expression, throwing an {@code IllegalArgumentException}
     * if the expression evaluates to {@code false}.
     * @param expression a boolean expression
     * @param message the exception message to use if the assertion fails
     * @throws ServiceException if {@code expression} is {@code false}
     */
    public static void isTrue(boolean expression, String message)  {
        if (!expression) {
            throw new ServiceException(FReply.FW_LOGIC_DATA_ERROR,message);
        }
    }




    /**
     * Assert that an object is {@code null}.
     * @param object the object to check
     * @param message the exception message to use if the assertion fails
     * @ if the object is not {@code null}
     */
    public static void isNull(@Nullable Object object, String message)  {
        if (object != null) {
            throw new ServiceException(FReply.FW_LOGIC_DATA_ERROR,message);
        }
    }




    /**
     * Assert that an object is not {@code null}.
     * @param object the object to check
     * @param message the exception message to use if the assertion fails
     * @throws ServiceException if the object is {@code null}
     */
    public static void notNull(@Nullable Object object, String message)  {
        if (object == null) {
            throw new ServiceException(FReply.FW_LOGIC_DATA_ERROR,message);
        }
    }


    /**
     * Assert that the given String is not empty; that is,
     * it must not be {@code null} and not the empty String.
     * @param text the String to check
     * @param message the exception message to use if the assertion fails
     * @throws ServiceException if the text is empty
     * @see StringUtils#hasLength
     */
    public static void hasLength(@Nullable String text, String message)  {
        if (!StringUtils.hasLength(text)) {
            throw new ServiceException(FReply.FW_LOGIC_DATA_ERROR,message);
        }
    }




    /**
     * Assert that the given String contains valid text content; that is, it must not
     * be {@code null} and must contain at least one non-whitespace character.
     * @param text the String to check
     * @param message the exception message to use if the assertion fails
     * @throws ServiceException if the text does not contain valid text content
     * @see StringUtils#hasText
     */
    public static void hasText(@Nullable String text, String message)  {
        if (!StringUtils.hasText(text)) {
            throw new ServiceException(FReply.FW_LOGIC_DATA_ERROR,message);
        }
    }





    /**
     * Assert that the given text does not contain the given substring.
     * @param textToSearch the text to search
     * @param substring the substring to find within the text
     * @param message the exception message to use if the assertion fails
     * @throws ServiceException if the text contains the substring
     */
    public static void doesNotContain(@Nullable String textToSearch, String substring, String message)  {
        if (StringUtils.hasLength(textToSearch) && StringUtils.hasLength(substring) &&
                textToSearch.contains(substring)) {
            throw new ServiceException(FReply.FW_LOGIC_DATA_ERROR,message);
        }
    }




    /**
     * Assert that an array contains elements; that is, it must not be
     * {@code null} and must contain at least one element.
     * @param array the array to check
     * @param message the exception message to use if the assertion fails
     * @throws ServiceException if the object array is {@code null} or contains no elements
     */
    public static void notEmpty(@Nullable Object[] array, String message)  {
        if (ObjectUtils.isEmpty(array)) {
            throw new ServiceException(FReply.FW_LOGIC_DATA_ERROR,message);
        }
    }





    /**
     * Assert that an array contains no {@code null} elements.
     * @param array the array to check
     * @param message the exception message to use if the assertion fails
     * @throws ServiceException if the object array contains a {@code null} element
     */
    public static void noNullElements(@Nullable Object[] array, String message)  {
        if (array != null) {
            for (Object element : array) {
                if (element == null) {
                    throw new ServiceException(FReply.FW_LOGIC_DATA_ERROR,message);
                }
            }
        }
    }




    /**
     * Assert that a collection contains elements; that is, it must not be
     * {@code null} and must contain at least one element.
     * @param collection the collection to check
     * @param message the exception message to use if the assertion fails
     * @throws ServiceException if the collection is {@code null} or
     * contains no elements
     */
    public static void notEmpty(@Nullable Collection<?> collection, String message)  {
        if (CollectionUtils.isEmpty(collection)) {
            throw new ServiceException(FReply.FW_LOGIC_DATA_ERROR,message);
        }
    }


    /**
     * Assert that a collection contains no {@code null} elements.
     * @param collection the collection to check
     * @param message the exception message to use if the assertion fails
     * @throws ServiceException if the collection contains a {@code null} element
     * @since 5.2
     */
    public static void noNullElements(@Nullable Collection<?> collection, String message)  {
        if (collection != null) {
            for (Object element : collection) {
                if (element == null) {
                    throw new ServiceException(FReply.FW_LOGIC_DATA_ERROR,message);
                }
            }
        }
    }



    /**
     * Assert that a Map contains entries; that is, it must not be {@code null}
     * and must contain at least one entry.
     * @param map the map to check
     * @param message the exception message to use if the assertion fails
     * @throws ServiceException if the map is {@code null} or contains no entries
     */
    public static void notEmpty(@Nullable Map<?, ?> map, String message)  {
        if (CollectionUtils.isEmpty(map)) {
            throw new ServiceException(FReply.FW_LOGIC_DATA_ERROR,message);
        }
    }





}
