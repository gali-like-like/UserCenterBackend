```java

public int compareTo(String anotherString) {
        byte v1[] = value;
        byte v2[] = anotherString.value; // 根据utf16或者latin1编码
        byte coder = coder();//获取当前编解码方式 -> 根据布尔值判断
        // 当前编码方式
        if (coder == anotherString.coder()) {
            return coder == LATIN1 ? StringLatin1.compareTo(v1, v2)
                                   : StringUTF16.compareTo(v1, v2);
        }
        return coder == LATIN1 ? StringLatin1.compareToUTF16(v1, v2)
                               : StringUTF16.compareToLatin1(v1, v2);
     }

```

```java

    byte coder() {
        return COMPACT_STRINGS ? coder : UTF16;
    }

```
## Latin1String 的比较方式
```java
@IntrinsicCandidate
    public static int compareTo(byte[] value, byte[] other) {
        int len1 = value.length;// /regedit
        int len2 = other.length;// code
        return compareTo(value, other, len1, len2);
    }

    public static int compareTo(byte[] value, byte[] other, int len1, int len2) {
        int lim = Math.min(len1, len2); 
        for (int k = 0; k < lim; k++) {
            if (value[k] != other[k]) { 
                return getChar(value, k) - getChar(other, k); // k= 10时,114>99 => /regedit > /code
            }
        }
        return len1 - len2;
    }
```

```java Latin1String 的getChar方法
public static char getChar(byte[] val, int index) {
        return (char)(val[index] & 0xff);
    }
```

```java
private static int binarySearch0(Object[] a, int fromIndex, int toIndex,
                                     Object key) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            @SuppressWarnings("rawtypes")
            Comparable midVal = (Comparable)a[mid];
            @SuppressWarnings("unchecked")
            int cmp = midVal.compareTo(key); // 第一次比较是114>99

            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1; // 第二轮high 0
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }
```
