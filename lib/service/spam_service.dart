import 'dart:developer';

import 'package:dio/dio.dart';
import 'package:my_native_plugin/models/jurnal_number.dart';
import 'package:my_native_plugin/models/spam_number.dart';
import 'package:my_native_plugin/my_native_plugin.dart';

class SpamService{
  MyNativePlugin plagin = MyNativePlugin();

  
  Future<bool?> updateDb()async{
    final result =  await plagin.updateDb();
    return result;
  }
  Future<bool?> updateDbISRunning()async{
    final result =  await plagin.updateDbISRunning();
    return result;
  }



  Future<List<JurnalNumber>> getCallLog()async{
    final result =  await plagin.getCallLog();
    return result;
  }
  Future<String?> getDescriptionFromAllScam(String number)async{
    final result =  await plagin.getDescriptionFromAllScam(number);
    return result;
  }

  Future<bool?> requestCallSucurePage()async{
    final result =  await plagin.requestCallSucurePage();
    return result;
  }

  
  Future<bool?> clearSpamDatabase()async {
    final result =  await plagin.clearSpamDatabase();
    return result;
  }
  Future<int> getSpamNumberCount()async {
    final result =  await plagin.getSpamNumberCount();
    return result;
  }
  Future<int?> countSpamNumbers()async {
    final result =  await plagin.countSpamNumbers();
    return result;
  }

  Future<bool> isCallScreeningRoleHeld()async{
    final result =  await plagin.isCallScreeningRoleHeld();
    return result;
  }

  Future<List<SpamNumber>> getAllScumCustomNumbers()async{
    final result =  await plagin.selectDatabaseCustomNumbers();
    if(result==null){
      return [];
    }
    List<SpamNumber> spamNumbers = [];

    for(int i=0; i<result.length;i++ ){

       final phoneNumberResult =  result[i]["number"];
       String phoneNumber;
       if(phoneNumberResult is! String){
        phoneNumber="empty";
       }else{
        phoneNumber=phoneNumberResult;
       }

       final descriptionResult =  result[i]["description"];;
       String description;
       if(descriptionResult is! String){
        description="empty";
       }else{
        description=phoneNumberResult;
       }
       SpamNumber currentSpanNumber = SpamNumber(number: phoneNumber,description: description,id: 0);
       spamNumbers.add(currentSpanNumber);
    }

    return spamNumbers;

  }
  
  Future<bool> insertSpamNumbers(List<SpamNumber> spamNumbers)async{
    return await plagin.insertSpamNumbers(spamNumbers)??true;
  }
  Future<bool> insertSpamCustomNumbers(List<SpamNumber> spamNumbers)async{
    return await plagin.insertSpamCustomNumbers(spamNumbers);
  }
  Future<bool> deleteCustomNumbersByNumber(SpamNumber spamNumbers)async{
    return await plagin.deleteCustomNumbersByNumber(spamNumbers);
  }

  Future<void> downLoad()async{
    final lastIdServer =  await plagin.countSpamNumbers();
    log("lastIdServer");
    log(lastIdServer.toString());
    final response =await  Dio().get(
      "https://call.stopscam.ai/api/v1/numberRouter/number_base_count",
      queryParameters: {
        "lastId":lastIdServer
      }
    );
    final summaryCount =  response.data["summaryCount"] as int;
    final batchSize =  response.data["batchSize"] as int;
    log(summaryCount.toString());
    log(batchSize.toString());
    int grade = ((summaryCount/batchSize)+1).toInt();
    log("grade");
    log(grade.toString());
    final response1 =await  Dio().get(
      "https://call.stopscam.ai/api/v1/numberRouter/number_base",
      queryParameters: {
        "startNumber":1,
        "endNumber":batchSize+1,
      }
    );
    log((response1.data as List<dynamic>).length.toString());
    log(response1.data.toString());
    for(int i=0;i<grade;i++){

    }
  }

  batchLoad(int counter,int oldLast)async{
    log(counter.toString());
    int? lastIdServer;
    //final lastIdServer =  await plagin.countSpamNumbers();
    if(counter==0){
      lastIdServer=1;
    }else{
      lastIdServer=oldLast;
    }
    log(lastIdServer.toString());
    final response =await  Dio().get(
      "https://call.stopscam.ai/api/v1/numberRouter/number_base",
      queryParameters: {
        "lastId":lastIdServer
      },
      onReceiveProgress: (count, total) {
        log("/////////////////");
        log(counter.toString());
        log(count.toString()+"/"+total.toString());
        log("/////////////////");
      },
    );
    log("lenth");
    final data = (response.data as List<dynamic>);
    final last = data.last["id"] as int;
    final length = (response.data as List<dynamic>).length;
    log((response.data as List<dynamic>).length.toString());
    if(length==0){
      return;
    }
    await batchLoad(counter+1,last);
  }
  Future<bool> insertAllow(SpamNumber number)async{
    return await plagin.insertAllow(number);
  }
  Future<List<SpamNumber>> getAllow()async{
    return await plagin.getAllow();
  }
  Future<bool> deleteAllow(String number)async{
    return await plagin.deleteAllow(number);
  }
  

}

// extension Gavno on String{
//   String gavno(){
//     return "Gavno";
//   }
// }