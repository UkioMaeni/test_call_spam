class JurnalNumber{
  String number;
  ETypeCall type;
  JurnalNumber({
    required this.number,
    required this.type,
  });

  static ETypeCall parseTypeFromInt(int type){
    switch(type){
      case 1: return ETypeCall.blocked;
      default: return ETypeCall.other;
    }
  }
}

enum ETypeCall{
  blocked,other
}

