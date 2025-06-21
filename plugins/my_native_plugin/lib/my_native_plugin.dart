
import 'package:my_native_plugin/models/jurnal_number.dart';
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
  Future<List<JurnalNumber>> getCallLog()async {
    return await MyNativePluginPlatform.instance.getCallLog();
  }
  Future<String?> getDescriptionFromAllScam(String number)async {
    return await MyNativePluginPlatform.instance.getDescriptionFromAllScam(number);
  }
  Future<bool?> updateDbISRunning()async {
    return await MyNativePluginPlatform.instance.updateDbISRunning();
  }
  Future<bool> insertAllow(SpamNumber spamNumber)async{
    final result = await MyNativePluginPlatform.instance.insertAllow(spamNumber);
    return result??false;
  }
  Future<bool> deleteAllow(String number)async{
    final result = await MyNativePluginPlatform.instance.deleteAllow(number);
    return result??false;
  }
  Future<List<SpamNumber>> getAllow()async{
    final result = await MyNativePluginPlatform.instance.getAllow();
    if(result==null){
      return [];
    }
    return result.map((e) => SpamNumber(number: e["number"], description: e["description"], id: 0)).toList();
  }
}
