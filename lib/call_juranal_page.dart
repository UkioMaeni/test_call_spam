import 'dart:developer';

import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_slidable/flutter_slidable.dart';
import 'package:my_native_plugin/models/jurnal_number.dart';
import 'package:my_native_plugin/models/spam_number.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:skeletonizer/skeletonizer.dart';
import 'package:test_call_spam/dialogs/info_spam_allow_dialog.dart';
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

  List<JurnalNumber> jurnal = [];

  loadContacts()async{
   jurnal= await spamService.getCallLog();
  setState(() {
    
  });
  }


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Color.fromRGBO(249, 250, 254, 1),
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
        child: Builder(
          builder: (context) {
            if(!granted){
              return Column(
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
              );
            }
            return ClipRRect(
              borderRadius: BorderRadius.circular(20),
              child: ColoredBox(
                color: Colors.white,
                child: Column(
                  children: [
                    Container(
                      height: 44,
                      child: Row(
                        children: [
                          Container(
                            width: 62,
                            alignment: Alignment.center,
                            child: Text("(***)")
                          ),
                          Container(
                            width: 140,
                            alignment: Alignment.centerLeft,
                            child: Text("NUMBER")
                          ),
                          Expanded(
                            child: Container(
                              width: 131,
                              alignment: Alignment.centerLeft,
                              child: Text("LABEL")
                            ),
                          ),
                        ],
                      ),
                    ),
                    Divider(color: Color.fromRGBO(234, 234, 234, 1),height: 1,),
                    Expanded(
                      child: ListView.separated(
                        itemCount: jurnal.length,
                        separatorBuilder: (context, index) {
                          return Divider(color: Color.fromRGBO(234, 234, 234, 1),height: 1,);
                        },
                        itemBuilder: (context, index) {
                
                          return NumberElement(jurnalNumber: jurnal[index],);
                        },
                      ),
                    ),
                  ],
                ),
              ),
            );
            return Image.asset("assets/people.png");
          },
        ),
      ),
    );
  }
}


class NumberElement extends StatefulWidget {
  final JurnalNumber jurnalNumber;
  const NumberElement({super.key,required this.jurnalNumber});

  @override
  State<NumberElement> createState() => _NumberElementState();
}

class _NumberElementState extends State<NumberElement> {

  SpamService spamService = SpamService();

  String? label;

  bool systemBloc = false;

  getDataLabel()async{
    if(widget.jurnalNumber.type==ETypeCall.blocked){
      final result = await spamService.getDescriptionFromAllScam(widget.jurnalNumber.number);
      if(result==null){
        label="System block";
        systemBloc=true;
      }else{
        label=result;
      }
    }else{
      label="";
    }
    setState(() {
      
    });
  }

  late String number;
  late ETypeCall type;
  @override
  void initState() {
    number=widget.jurnalNumber.number;
    type=widget.jurnalNumber.type;
    getDataLabel();
    super.initState();
  }





  Future<void> callBack()async{
    bool isBlocked = type==ETypeCall.blocked;
    await showDialog(
      
      context: context,
      builder: (context) {
        return InfoSpamAllowDialog(
          number: number,
          label: label??"not",
          isBlocked: isBlocked,
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
     bool isBlocked = type==ETypeCall.blocked;
     if(number.isEmpty){
      number="Номер скрыт";
     }
     return HorizontalSlice(
      enabled: label!=null && !systemBloc,
      dissmisible: callBack,
      maxOffset: 120,
      action: Container(
        height: 56,
        color:isBlocked?Color.fromRGBO(206, 243, 221, 1): Color.fromRGBO(243, 206, 206, 1),
        child: Center(
          child: Text(isBlocked?"Allow":"Report",style: TextStyle(color:isBlocked?Colors.green:Colors.red),)
        ),
      ),
      child: Container(
          height: 56,
          
          child: Row(
            children: [
              Container(
                width: 62,
                alignment: Alignment.center,
                child: Container(
                  width: 13,
                  height: 13,
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(13),
                    color: isBlocked?Colors.red:Colors.green
                  ),
                )
              ),
              Container(
                width: 140,
                alignment: Alignment.centerLeft,
                child: Text(number)
              ),
              Expanded(
                child: Container(
                  width: 131,
                  alignment: Alignment.centerLeft,
                  child: Builder(
                    builder: (context) {
                      if(label==null){
                        return Skeletonizer(
                          enabled: true,
                          child: const Text('Subtitle here'),
                        );
                      }
                      return Text(label!);
                    },
                  )
                ),
              ),
            ],
          ),
        ),
     );
  }
}



class HorizontalSlice extends StatefulWidget {
  final double maxOffset;
  final Widget child;
  final Widget action;
  final bool enabled;
  final Future<void> Function() dissmisible;
  const HorizontalSlice({super.key,required this.maxOffset,required this.action,required this.child,required this.dissmisible,required this.enabled});

  @override
  State<HorizontalSlice> createState() => _HorizontalSliceState();
}

class _HorizontalSliceState extends State<HorizontalSlice> with SingleTickerProviderStateMixin {

  late final AnimationController _ctrl;
      
  double _dragStartX = 0;

  @override
  void initState() {
    _ctrl=AnimationController(vsync: this, duration: const Duration(milliseconds: 200));
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () {
        showBottomSheet(
           context: context,
          builder: (context) {
            return Container(
              child: Column(
                children: [
                  
                ],
              ),
            );
          },
        );
      },
      onHorizontalDragStart: (d) => _dragStartX = d.localPosition.dx,
      onHorizontalDragUpdate: (d) {
        if(!widget.enabled){
          return;
        }
        final delta = _dragStartX - d.localPosition.dx;   // вправо->0, влево->+
        if (delta > 0) {
          _ctrl.value = (delta / 120).clamp(0, 1);
          setState(() {
            
          });
        }
      },
      onHorizontalDragEnd: (d) async{
        if(!widget.enabled){
          return;
        }
        log(_ctrl.value.toString());
        if (_ctrl.value > 0.8) {
          
          await widget.dissmisible();
          _ctrl.animateTo(0);
        } else {
          // Отпустили раньше – возвращаем назад
          _ctrl.animateBack(0);
        }
      },
      child: Stack(
        alignment: Alignment.centerRight,
        children: [
          
          // Positioned.fill(
          //   child: AnimatedBuilder(
          //     animation: _ctrl,
          //     builder: (ctx, _){
          //      return LayoutBuilder(
          //        builder: (context, constraints) {
          //         final maxWidth = constraints.maxWidth;
          //         final maxHeight = constraints.maxHeight;
                  
          //          return Transform.translate(
          //             offset: Offset(maxWidth-_ctrl.value*120, 0),
          //             child: SizedBox(
          //               width: widget.maxOffset,
          //               child: widget.action,
          //             ),
          //           );
          //        }
          //      );
          //     }
          //   ),
          // ),
          Positioned(
            right: 0,
            bottom: 0,
            child: LayoutBuilder(
              builder: (context, constraints) {
              final maxWidth = constraints.maxWidth;
              final maxHeight = constraints.maxHeight;
              
                return SizedBox(
                  width: widget.maxOffset,
                  child: widget.action,
                );
              }
            )
          ),
          AnimatedBuilder(
            animation: _ctrl,
            builder: (ctx, _) => Transform.translate(
              offset: Offset(-_ctrl.value * 120, 0),
              child: ColoredBox(
                color: Colors.white,
                child: SizedBox(
                  width: double.infinity,
                  child: widget.child,
                ),
              ),
            ),
          ),
          
        ],
      ),
    );
  }
}