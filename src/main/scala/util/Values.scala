package scadulix
package util

// this will hopefully be in scala stdlib soon
// see: https://github.com/scala/scala/pull/81
trait Values {
  def values[A: Manifest]: Iterable[A] = nameValueMap[A].values

  private def nameValueMap[A: Manifest]: Map[String,A] = {
    val fields = getClass.getDeclaredFields

    val methods = getClass.getMethods filter { method =>
      method.getParameterTypes.isEmpty &&
      manifest.erasure.isAssignableFrom(method.getReturnType) &&
      method.getDeclaringClass != classOf[Values] &&
      fields.exists { field =>
        field.getName == method.getName && field.getType == method.getReturnType
      }
    }

    methods map { method =>
      val name = method.getName
      val value = method.invoke(this).asInstanceOf[A]
      (name -> value)
    } toMap
  }

  final def byName[A: Manifest](s: String): Option[A] = nameValueMap[A].get(s)
}
