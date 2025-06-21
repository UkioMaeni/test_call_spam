import 'dart:developer';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:my_native_plugin/models/spam_number.dart';
import 'package:test_call_spam/new_contact.dart';
import 'package:test_call_spam/service/spam_service.dart';

class AllowListPage extends StatefulWidget {
  const AllowListPage({super.key});

  @override
  State<AllowListPage> createState() => _AllowListPageState();
}

class _AllowListPageState extends State<AllowListPage> {

  SpamService spamService = SpamService();

    List<SpamNumber>? allowNumbers;

    Future<void> getAllowNumbers()async{
      final numbers= await spamService.getAllow();
      log(numbers.length.toString());
      allowNumbers=numbers;
      setState(() {
        
      });
    }

    @override
    void initState(){
      getAllowNumbers();
      super.initState();
    }

  Future<void> deleteItem(SpamNumber number)async{
    final result =  await spamService.deleteAllow(number.number);
    if(result){
      getAllowNumbers();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
                'Allow list',
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
                if(allowNumbers==null){
                  return Center(child: CircularProgressIndicator(),);
                }
                return Expanded(
                  child: Column(
                    children: [
                      Expanded(
                        child: Builder(
                          builder: (context) {
                            if(allowNumbers!.isEmpty){
                              return Image.asset("assets/people.png");
                            }
                            return ListView.builder(
                              itemCount:allowNumbers!.length ,
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
                                        "${allowNumbers![index].number} : ${allowNumbers![index].description}",
                                        style: TextStyle(
                                          fontSize: 18
                                        ),
                                      ),
                                      GestureDetector(
                                        onTap: ()=>deleteItem(allowNumbers![index]),
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
                          Navigator.push(context,MaterialPageRoute(builder: (context) => NewContact(onChange: getAllowNumbers,type: NewContactType.allow,),));
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
              },
            ),

          ],
        ),
      ),
    );
  }
}