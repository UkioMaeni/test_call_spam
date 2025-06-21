import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:my_native_plugin/models/spam_number.dart';
import 'package:test_call_spam/service/spam_service.dart';

class InfoSpamAllowDialog extends StatefulWidget {
  final String number;
  final String label;
  final bool isBlocked;
  const InfoSpamAllowDialog({super.key,required this.number,required this.label,required this.isBlocked});

  @override
  State<InfoSpamAllowDialog> createState() => _InfoSpamAllowDialogState();
}

class _InfoSpamAllowDialogState extends State<InfoSpamAllowDialog> {


  TextEditingController _textEditingController = TextEditingController();

  SpamService spamService = SpamService();


  bool isSending = false;

  bool isSend = false;

  sendREport()async{
    try {
      if(isSending) return;
      isSending=true;
      setState(() {
        
      });
      if(isBlock){
        await spamService.insertSpamCustomNumbers([SpamNumber(number: widget.number, description: _textEditingController.text, id: 0)]);
      }

      final result = await Dio().post(
        "https://call.stopscam.ai/api/v1/numberRouter/report",
        data: {
          "number":widget.number,
          "text":_textEditingController.text
        }
      );
      
    } catch (e) {
      
    }finally{
      _textEditingController.text="";
      isSend=true;
      setState(() {
        
      });
      Future.delayed(Duration(seconds: 2)).then((value) => Navigator.pop(context));
    }
  }

  @override
  void dispose() {
    _textEditingController.dispose();
    super.dispose();
  }

  bool isBlock=false;


bool isAllow = false;

  allow()async{
    try {
      
    } catch (e) {
      
    }finally{
        isAllow=true;
        setState(() {
          
        });
        Future.delayed(Duration(seconds: 2)).then((value) => Navigator.pop(context));
    }
  }

  @override
  Widget build(BuildContext context) {
    return PopScope(
      canPop: !isSending,
      child: Dialog(
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20)
            ),
            insetPadding: EdgeInsets.zero,
            alignment: Alignment.center,
            child:  ClipRRect(
              borderRadius: BorderRadius.circular(20),
              child: Builder(
                builder: (context) {
                  return Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Builder(
                        builder: (context) {
                          if(isSend){
                            return Container(
                              constraints: BoxConstraints(
                                maxWidth: MediaQuery.of(context).size.width*0.8,
                              ),
                              height: 60,
                              alignment: Alignment.center,
                              child: Text("sended!"),
                            );
                          }
                          if(isAllow){
                            return Container(
                              constraints: BoxConstraints(
                                maxWidth: MediaQuery.of(context).size.width*0.8,
                              ),
                              height: 60,
                              alignment: Alignment.center,
                              child: Text("allow!"),
                            );
                          }
                          return Container(
                            padding: EdgeInsets.all(16),
                            constraints: BoxConstraints(
                              maxWidth: MediaQuery.of(context).size.width*0.8,
                          
                            ),
                            color:Color.fromRGBO(249, 250, 254, 1),
                            child: Column(
                              children: [
                                Text(widget.isBlocked?"Allow":"Report"),
                                SizedBox(height: 20,),
                                Container(
                                  decoration: BoxDecoration(
                                    color: Colors.white,
                                    borderRadius: BorderRadius.circular(20)
                                  ),
                                  padding: EdgeInsets.all(16),
                                  child: Column(
                                    crossAxisAlignment: CrossAxisAlignment.start,
                                    children: [
                                      Row(
                                        children: [
                                          Text(widget.number),
                                          Expanded(child: SizedBox()),
                                          Container(
                                            width: 13,
                                            height: 13,
                                            decoration: BoxDecoration(
                                              borderRadius: BorderRadius.circular(13),
                                              color: widget.isBlocked?Colors.red:Colors.green
                                            ),
                                          ),
                                          Container(
                                            width: 13,
                                            height: 13,
                                            decoration: BoxDecoration(
                                              borderRadius: BorderRadius.circular(13),
                                              color: widget.isBlocked?Colors.red:Colors.green
                                            ),
                                          ),
                                          Container(
                                            width: 13,
                                            height: 13,
                                            decoration: BoxDecoration(
                                              borderRadius: BorderRadius.circular(13),
                                              color: widget.isBlocked?Colors.red:Colors.green
                                            ),
                                          )
                                        ],
                                      ),
                                      SizedBox(height: 10,),
                                      Text("LABEL"),
                                      SizedBox(height: 10,),
                                      Text(widget.label??"not"),
                                      SizedBox(height: 10,),
                                      
                                    ],
                                  ),
                                ),
                                if(!widget.isBlocked)Column(
                                  children: [
                                    SizedBox(height: 10,),
                                    GestureDetector(
                                      onTap: () {
                                        setState(() {
                                          isBlock=!isBlock;
                                        });
                                      },
                                      child: Container(
                                        color: Colors.transparent,
                                        child: Row(
                                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                          children: [
                                            Container(
                                              width: 20,
                                              height: 20,
                                              decoration: BoxDecoration(
                                                border: Border.all(
                                        
                                                ),
                                                color:isBlock?Colors.red:null 
                                              ),
                                            ),
                                            Text("заблокировать на устрйостве")
                                          ],
                                        ),
                                      ),
                                    ),
                                    SizedBox(height: 10,),
                                    Text("TExt:"),
                                    Container(
                                
                                      decoration: BoxDecoration(
                                        border: Border.all(),
                                        borderRadius: BorderRadius.circular(10)
                                      ),
                                      padding: EdgeInsets.all(10),
                                      child: TextFormField(
                                        maxLines: null,
                                        minLines: null,
                                        maxLength: 2000,
                                        keyboardType: TextInputType.multiline,
                                        controller: _textEditingController,
                                        
                                        decoration: InputDecoration(
                                          contentPadding: EdgeInsets.zero,
                                          counter: SizedBox.shrink(),
                                          border: InputBorder.none,
                                          constraints: BoxConstraints(
                                            minHeight: 46,
                                            maxHeight: 120
                                          ),
                                        ),
                                      ),
                                    ),
                                    SizedBox(height: 20,),
                                    GestureDetector(
                                      onTap:()=> sendREport(),
                                      child: Container(
                                        height: 40,
                                        width: 80,
                                        decoration: BoxDecoration(
                                          color: Colors.green,
                                          borderRadius: BorderRadius.circular(5)
                                        ),
                                        alignment: Alignment.center,
                                        child: Builder(
                                          builder: (context) {
                                            if(isSending){
                                              return CircularProgressIndicator();
                                            }
                                            return Text("Send report",style: TextStyle(color: Colors.white),);
                                          }
                                        ),
                                      ),
                                    ),
                                  ],
                                ),
                                if(widget.isBlocked)
                                GestureDetector(
                                      onTap:()=> allow(),
                                      child: Container(
                                        height: 40,
                                        width: 80,
                                        decoration: BoxDecoration(
                                          color: Colors.green,
                                          borderRadius: BorderRadius.circular(5)
                                        ),
                                        alignment: Alignment.center,
                                        child: Builder(
                                          builder: (context) {
                                            if(isSending){
                                              return CircularProgressIndicator();
                                            }
                                            return Text("Allow",style: TextStyle(color: Colors.white),);
                                          }
                                        ),
                                      ),
                                    ),
                                
                                // GestureDetector(
                                //       onTap:()=> Navigator.pop(context),
                                //       child: Container(
                                //          height: 40,
                                //         width: 80,
                                //         decoration: BoxDecoration(
                                //           borderRadius: BorderRadius.circular(5),
                                //           border: Border.all(
                                //             color: Colors.red,
                                //             width: 3
                                //           ),
                                //         ),
                                //         alignment: Alignment.center,
                                //         child: Text("close",style: TextStyle(color: Colors.red),),
                                //       ),
                                //     ),
                              ],
                            ),
                          );
                        }
                      ),
                    ],
                  );
                }
              ),
            )
          ),
    );
  }
}