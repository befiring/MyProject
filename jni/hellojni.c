#include<jni.h>
#include<com_example_contact_LoadJni.h>
/*
*Class: com_example_nativetest_NativeClass
*Method: nativeGetResult
*Signature: (I)I
*/
JNIEXPORT jstring JNICALL Java_com_example_contact_LoadJni_getResult
(JNIEnv* env, jclass obj)
{
return  (*env)->NewStringUTF(env,(char*)"This is JniDemo !");
}