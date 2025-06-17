import 'dart:developer';

import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:my_native_plugin/models/spam_number.dart';
import 'package:test_call_spam/new_contact.dart';
import 'package:test_call_spam/service/spam_service.dart';

class DbDownloadPage extends StatefulWidget {
  const DbDownloadPage({super.key});

  @override
  State<DbDownloadPage> createState() => _DbDownloadPageState();
}

class _DbDownloadPageState extends State<DbDownloadPage> {

  SpamService spamService = SpamService();

    List<SpamNumber>? customSpams;


    @override
    void initState(){
      init();
      super.initState();
    }


  int? count;

  init()async{
    final countNew =  await spamService.getSpamNumberCount();
    log(count.toString());
    setState(() {
      count=countNew;
    });
  }

  bool isDownload= false;

  download()async{
    if(isDownload) return;
    isDownload=true;
    setState(() {
      
    });
    await spamService.updateDb();
    //isDownload=false;
  }
  
  clear()async{
    await spamService.clearSpamDatabase();
    ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text("clear")));
  }




  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
                'DbDownloadPage',
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
                if(count==null){
                  return Center(child: CircularProgressIndicator(),);
                }
                return Expanded(
                  child: Column(
                    children: [
                      Expanded(
                        child: Builder(
                          builder: (context) {
                            if(count==0){
                              return Column(
                                children: [
                                  Image.asset("assets/people.png"),
                                  Text("пусто")
                                ],
                              );
                            }
                            return Center(
                              child: Text(count!.toString()+" zapisey"),
                            );
                            return ListView.builder(
                              itemCount:customSpams!.length ,
                              itemBuilder: (context, index) {
                                return Container(
                                  height: 40,
                                  margin: EdgeInsets.symmetric(vertical: 5),
                                  decoration: BoxDecoration(
                                    color: Color.fromARGB(255, 212, 208, 208),
                                    borderRadius: BorderRadius.circular(10)
                                  ),
                                  child: Row(
                                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                    children: [
                                      Text(
                                        "${customSpams![index].number} : ${customSpams![index].description}",
                                        style: TextStyle(
                                          fontSize: 18
                                        ),
                                      ),
                                      GestureDetector(
                                        child: Container(
                                          height: 40,
                                          width: 50,
                                          color: Colors.blue,
                                          child: Text("delete"),
                                        ),
                                      )
                                    ],
                                  ),
                                );
                              },
                            );
                          }
                        )
                      ),
                      GestureDetector(
                        onTap:download,
                        child: Container(
                          height: 60,
                          width: double.infinity,
                          decoration: BoxDecoration(
                            color:isDownload?Colors.red: Colors.blue
                          ),
                          alignment: Alignment.center,
                          child: Text(
                            "download"
                          ),
                        ),
                      ),
                      SizedBox(height: 40,),
                      GestureDetector(
                        onTap:clear,
                        child: Container(
                          height: 60,
                          width: double.infinity,
                          decoration: BoxDecoration(
                            color: Colors.blue
                          ),
                          alignment: Alignment.center,
                          child: Text(
                            "clear"
                          ),
                        ),
                      ),
                      SizedBox(height: 40,)
                    ],
                  ),
                );
                if(customSpams!.isEmpty){
                  Image.asset("assets/people.png");
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