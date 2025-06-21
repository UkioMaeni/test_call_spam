import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:my_native_plugin/models/spam_number.dart';
import 'package:test_call_spam/service/spam_service.dart';

enum NewContactType{
  allow,custom
}

class NewContact extends StatefulWidget {
  final NewContactType type;
  final Function() onChange;
  const NewContact({super.key,required this.onChange,required this.type});

  @override
  State<NewContact> createState() => _NewContactState();
}

class _NewContactState extends State<NewContact> {

  TextEditingController numberController = TextEditingController();
  TextEditingController cause = TextEditingController();

  @override
  void dispose() {
    numberController.dispose();
    cause.dispose();
    super.dispose();
  }

  addNumber()async{
    if(widget.type==NewContactType.custom){
      final result =  await SpamService().insertSpamCustomNumbers([SpamNumber(number: numberController.text, description: cause.text,id: 0)]);
      if(result){
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text("Успешно"),duration: Duration(seconds: 3),));
        numberController.text="";
        cause.text="";
        setState(() {
          
        });
        widget.onChange();
      }
    }else{
       final result =  await SpamService().insertAllow(SpamNumber(number: numberController.text, description: cause.text,id: 0));
      if(result){
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text("Успешно"),duration: Duration(seconds: 3),));
        numberController.text="";
        cause.text="";
        setState(() {
          
        });
        widget.onChange();
      }
    }
    
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title:Text("add new contact"),

      ),
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16),
        child: Column(
          children: [
            Text("Номер"),
            Container(
              height: 50,
              width: double.infinity,
              decoration: BoxDecoration(
                border: Border.all(
                  color: Colors.black
                )
              ),
              child: TextFormField(
                controller: numberController,
              ),
            ),
            Text("Причина"),
            Container(
              height: 50,
              width: double.infinity,
              decoration: BoxDecoration(
                border: Border.all(
                  color: Colors.black
                )
              ),
              child: TextFormField(
                controller: cause,
              ),
            ),
            Expanded(child: SizedBox.shrink()),
            GestureDetector(
              onTap: () {
                
                addNumber();
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
      ),
    );
  }
}