import 'package:my_native_plugin/models/spam_number.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'my_native_plugin_method_channel.dart';

abstract class MyNativePluginPlatform extends PlatformInterface {
  /// Constructs a MyNativePluginPlatform.
  MyNativePluginPlatform() : super(token: _token);

  static final Object _token = Object();

  static MyNativePluginPlatform _instance = MethodChannelMyNativePlugin();

  /// The default instance of [MyNativePluginPlatform] to use.
  ///
  /// Defaults to [MethodChannelMyNativePlugin].
  static MyNativePluginPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [MyNativePluginPlatform] when
  /// they register themselves.
  static set instance(MyNativePluginPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  
  Future<bool?> requestCallSucurePage() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
  Future<bool?> isCallScreeningRoleHeld() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
  Future<int?> getSpamNumberCount() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
  
  Future<int?> countSpamNumbers()async {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
  Future<bool?> insertSpamNumbers(List<SpamNumber> spamNumbers) {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
  Future<bool?> clearSpamDatabase() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
  Future<bool?> setEnableBlocking(bool enabled){
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
  Future<bool?> getEnableBlocking(){
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
  Future<bool?> isBlockingEnabled(){
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
  Future<bool?> insertSpamCustomNumbers(List<SpamNumber> spamNumbers) {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
  Future<List<Map<String,dynamic>>?> selectDatabaseCustomNumbers(){
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
  Future<bool?> deleteCustomNumbersByNumber(SpamNumber spamNumber){
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
  Future<bool?> deleteDatabaseCustomNumbers(){
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
  Future<bool?> updateDb(){
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
  Future<bool?> getCallLog(){
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
