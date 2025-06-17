import 'dart:developer';

import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:my_native_plugin/models/spam_number.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:test_call_spam/new_contact.dart';
import 'package:test_call_spam/service/spam_service.dart';

class CallJurnalPage extends StatefulWidget {
  const CallJurnalPage({super.key});

  @override
  State<CallJurnalPage> createState() => _CallJurnalPageState();
}

class _CallJurnalPageState extends State<CallJurnalPage> {

  SpamService spamService = SpamService();



    @override
    void initState(){
      init();
      super.initState();
    }


  bool granted=false;

  init()async{
    await phonePermissionCheck();
  }

  phonePermissionCheck()async{
    final status =  await Permission.phone.request();
    if(!status.isGranted){
      granted = false;
    }else{
      granted = true;
      loadContacts();
    }
    setState(() {
      
    });
  }

  

  loadContacts()async{
    await spamService.getCallLog();
  }


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
                'CallJurnalPage',
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                  color: Color.fromRGBO(31, 31, 31, 1)
                ),
              ),
      ),
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16),
        child: Column(
          children: [
            
            Builder(
              builder: (context) {
                if(!granted){
                  return Expanded(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text("Нет прав"),
                        SizedBox(height: 10,),
                        GestureDetector(
                          onTap: phonePermissionCheck,
                          child: Container(
                            height: 50,
                            width: 40,
                            color: Colors.blueAccent,
                            child: Text("запросить"),
                          ),
                        )
                      ],
                    )
                  );
                }
                return Image.asset("assets/people.png");
              },
            ),

          ],
        ),
      ),
    );
  }
}


class DialofDownLoad extends StatefulWidget {
  const DialofDownLoad({super.key});

  @override
  State<DialofDownLoad> createState() => _DialofDownLoadState();
}

class _DialofDownLoadState extends State<DialofDownLoad> {

  SpamService spamService = SpamService();


  String step ="load";

  int countProcess = 0;
  int totalProcess = 1;
  bool isReciver = false;

  List<SpamNumber> numbers=[];


  load()async{

    final lastIdServer =  await spamService.countSpamNumbers();

    final response =await  Dio().get(
      "https://call.stopscam.ai/api/v1/numberRouter/number_base",
      queryParameters: {
        "lastId":lastIdServer
      },
      onReceiveProgress: (count, total) {
        isReciver = true;
        countProcess=count;
        totalProcess=total;
        log(count.toString()+"/"+total.toString());
        setState(() {
          
        });
      },
    );
    final data= response.data as List<dynamic>;
    numbers = data.map((element) => SpamNumber(number: element["number"], description: element["description"],id: element["id"])).toList();
    step="db";
    setState(() {
      
    });
    toDb();
  }

  int chank = 0;
  int totalChunk = 0;

  toDb()async{

    final batchSize = 100000;

    for (int i = 0; i < numbers.length; i += batchSize) {
    // 1) формируем срез [i, i + batchSize)
    final chunk = numbers.sublist(
      i,
      i + batchSize > numbers.length ? numbers.length : i + batchSize,
    );

    // 2) вставляем и ждём завершения
    await spamService.insertSpamNumbers(chunk);
    
    // 3) (опционально) показываем прогресс
    chank=i + chunk.length;
    totalChunk=numbers.length;
    setState(() {
      
    });
    debugPrint('Inserted ${i + chunk.length} / ${numbers.length}');
    chunk.clear();
  }
  numbers.clear();
  setState(() {
    step="well";
  });
  }

  @override
  void initState() {
    load();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    
    return PopScope(
      canPop: false,
      child: Dialog(
        
        child: Container(
          height: 400,
          child: Column(
            children: [
              Expanded(
                child: Builder(
                  builder: (context) {
                    if(step=="load"){
                      return Column(
                        children: [
                          Text("Загрузка"),
                          Builder(
                            builder: (context) {
                              if(!isReciver){
                                return Text("Предворительная загрузка");
                              }
                              return Column(
                                children: [
                                  Text((countProcess/totalProcess*100).toStringAsFixed(1)+"%"),
                                  Text("total: " +(totalProcess/1024/1024).toString()+" Мб"),
                                ],
                              );
                            },
                          )
                          
                        ],
                      );
                    }
                    if(step=="db"){
                      return Column(
                        children: [
                          Text("Сохранение в локальную базу"),
                          Text('Inserted ${chank} / ${totalChunk}'),
                        ],
                      );
                    }
                    if(step=="well"){
                      return Column(
                        children: [
                          Text("Готово"),
                          //Text((countProcess/countProcess).toString()),
                        ],
                      );
                    }
                    return SizedBox.shrink();
                  },
                )
              ),
              GestureDetector(
                onTap: () {
                  Navigator.pop(context);
                },
                child: Container(
                  height: 40,
                  width: 60,
                  color: Colors.amber,
                ),
              )
            ],
          ),
        ),
      ),
    );
  }
}