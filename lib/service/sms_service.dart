import 'package:dio/dio.dart';

class SMSService{
  Future<SMSData?> checkSms()async{
    try {
      final result =await Dio().post(
        "https://call.stopscam.ai/api/v1/sms/check"
      );
      final data = result.data;
      return SMSData(
        category: data["category"]??"",
        riskLevel: data["risk_level"]??-1,
        explanation: data["explanation"]??"",
      );
    } catch (e) {
      print(e);
      return null;
    }
  }
}

class SMSData{
  String category;
  int riskLevel;
  String explanation;
  SMSData({
    required this.category,
    required this.riskLevel,
    required this.explanation,
  });
}