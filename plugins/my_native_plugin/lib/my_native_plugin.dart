
import 'package:my_native_plugin/models/spam_number.dart';

import 'my_native_plugin_platform_interface.dart';

class MyNativePlugin {
  Future<bool?> requestCallSucurePage() async{
    return await MyNativePluginPlatform.instance.requestCallSucurePage()??true;
  }
  Future<bool> isCallScreeningRoleHeld() async{
    return await MyNativePluginPlatform.instance.isCallScreeningRoleHeld()??false;
  }

  Future<int> getSpamNumberCount() async{
    return await MyNativePluginPlatform.instance.getSpamNumberCount()??0;
  }
  

  Future<int?> countSpamNumbers()async {
    return await MyNativePluginPlatform.instance.countSpamNumbers();
  }
  Future<bool?> insertSpamNumbers(List<SpamNumber> spamNumbers)async {
    return await MyNativePluginPlatform.instance.insertSpamNumbers(spamNumbers);
  }
  Future<bool?> clearSpamDatabase() async{
    return await MyNativePluginPlatform.instance.clearSpamDatabase();
  }
  Future<bool?> setEnableBlocking(bool enabled)async {
    return await MyNativePluginPlatform.instance.setEnableBlocking(enabled);
  }
  Future<bool> getEnableBlocking()async {
    return await MyNativePluginPlatform.instance.getEnableBlocking()??false;
  }
  Future<bool?> isBlockingEnabled()async {
    return await MyNativePluginPlatform.instance.isBlockingEnabled();
  }
  //кастомные номера
  Future<bool> insertSpamCustomNumbers(List<SpamNumber> spamNumbers)async {
    return await MyNativePluginPlatform.instance.insertSpamCustomNumbers(spamNumbers)??false;
  }
  Future<List<Map<String,dynamic>>?> selectDatabaseCustomNumbers()async {
    return await MyNativePluginPlatform.instance.selectDatabaseCustomNumbers();
  }
  Future<bool> deleteCustomNumbersByNumber(SpamNumber spamNumber)async {
    return await MyNativePluginPlatform.instance.deleteCustomNumbersByNumber(spamNumber)??false;
  }
  Future<bool?> deleteDatabaseCustomNumbers()async {
    return await MyNativePluginPlatform.instance.deleteDatabaseCustomNumbers();
  }
  Future<bool?> updateDb()async {
    return await MyNativePluginPlatform.instance.updateDb();
  }
  Future<bool?> getCallLog()async {
    return await MyNativePluginPlatform.instance.getCallLog();
  }
  
}
