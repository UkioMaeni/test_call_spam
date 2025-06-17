import 'dart:developer';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:my_native_plugin/models/spam_number.dart';

import 'my_native_plugin_platform_interface.dart';

/// An implementation of [MyNativePluginPlatform] that uses method channels.
class MethodChannelMyNativePlugin extends MyNativePluginPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('my_native_plugin');
  @override
  Future<bool?> requestCallSucurePage() async {
    final version = await methodChannel.invokeMethod<bool>('requestCallScreeningRole');
    return version;
  }
  @override
  Future<bool?> isCallScreeningRoleHeld() async {
    final isGranted = await methodChannel.invokeMethod<bool>('isCallScreeningRoleHeld');
    return isGranted;
  }
  @override
  Future<int?> getSpamNumberCount() async{
    final count = await methodChannel.invokeMethod<int>('getSpamNumberCount');
    return count;
  }

  @override
  Future<int?> countSpamNumbers()async {
    return await methodChannel.invokeMethod<int>('countSpamNumbers');
  }

  @override
  Future<bool?> insertSpamNumbers(List<SpamNumber> spamNumbers) async{
    final result = await methodChannel.invokeMethod<bool>('insertSpamNumbers',{
      'numbers':spamNumbers.map((e) => e.toMap()).toList()
    });
    return result;
  }
  @override
  Future<bool?> clearSpamDatabase() async{
    final result = await methodChannel.invokeMethod<bool>('clearSpamDatabase');
    return result;
  }
  @override
  Future<bool?> setEnableBlocking(bool enabled) async{
    final result = await methodChannel.invokeMethod<bool>('setCallBlockingEnabled',{'enabled': enabled});
    return result;
  }
  @override
  Future<bool?> getEnableBlocking() async{
    final result = await methodChannel.invokeMethod<bool>('getCallBlockingEnabled');
    return result;
  }
  @override
  Future<bool?> isBlockingEnabled() async{
    final result = await methodChannel.invokeMethod<bool>('isCallBlockingEnabled');
    return result;
  }
 
  @override
  Future<bool?> insertSpamCustomNumbers(List<SpamNumber> spamNumbers) async{
    final result = await methodChannel.invokeMethod<bool>('insertSpamCustomNumbers',{
      'numbers':spamNumbers.map((e) => e.toMap()).toList()
    });
    return result;
  }
  @override
  Future<List<Map<String,dynamic>>> selectDatabaseCustomNumbers() async{
    final result = await methodChannel.invokeMethod<List<dynamic>>('selectDatabaseCustomNumbers')??[];
    log("selectDatabaseCustomNumbers");
    log(result.toString());
    List<Map<String,dynamic>> returnedList=[]; 
    for(var res in result){
      if(res is Map){
        returnedList.add({"number":res["number"],"description":res["description"]});
      }
    }
    log("selectDatabaseCustomNumbersREt");
    log(returnedList.toString());
    return returnedList;
  }
  @override
  Future<bool?> deleteCustomNumbersByNumber(SpamNumber spamNumber)async{
    final result = await methodChannel.invokeMethod<bool>('deleteCustomNumbersByNumber',{"number":spamNumber.number});
    return result;
  }
  @override
  Future<bool?> deleteDatabaseCustomNumbers() async{
    final result = await methodChannel.invokeMethod<bool>('deleteDatabaseCustomNumbers');
    return result;
  }

  //updateDb
  @override
  Future<bool?> updateDb() async{
    final result = await methodChannel.invokeMethod<bool>('updateDb');
    return result;
  }
  @override
  Future<bool?> getCallLog() async{
    final result = await methodChannel.invokeMethod<dynamic>('getCallLog');
    log(result.toString());
    return true;
  }
}
