import 'dart:developer';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:my_native_plugin/models/spam_number.dart';
import 'package:test_call_spam/new_contact.dart';
import 'package:test_call_spam/service/spam_service.dart';

class CallPage extends StatefulWidget {
  const CallPage({super.key});

  @override
  State<CallPage> createState() => _CallPageState();
}

class _CallPageState extends State<CallPage> {

  SpamService spamService = SpamService();

    List<SpamNumber>? customSpams;

    Future<void> getAllCustomNumbers()async{
      final numbers= await spamService.getAllScumCustomNumbers();
      log(numbers.length.toString());
      customSpams=numbers;
      setState(() {
        
      });
    }

    @override
    void initState(){
      getAllCustomNumbers();
      super.initState();
    }

  Future<void> deleteItem(SpamNumber number)async{
    final result =  await spamService.deleteCustomNumbersByNumber(number);
    if(result){
      getAllCustomNumbers();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
                'Your contacts',
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
                if(customSpams==null){
                  return Center(child: CircularProgressIndicator(),);
                }
                return Expanded(
                  child: Column(
                    children: [
                      Expanded(
                        child: Builder(
                          builder: (context) {
                            if(customSpams!.isEmpty){
                              return Image.asset("assets/people.png");
                            }
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
                                        onTap: ()=>deleteItem(customSpams![index]),
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
                        onTap: () {
                          Navigator.push(context,MaterialPageRoute(builder: (context) => NewContact(onChange: getAllCustomNumbers,),));
                        },
                        child: Container(
                          height: 80,
                          width: double.infinity,
                          decoration: BoxDecoration(
                            color: Colors.blue
                          ),
                          child: Text(
                            "add new contact"
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