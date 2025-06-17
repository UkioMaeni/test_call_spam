class SpamNumber{
  String number;
  String description;
  int id;
  SpamNumber({
    required this.number,
    required this.description,
    required this.id,
  });

  Map<String, dynamic> toMap(){
    return {
      "number":number,
      "description":description,
      "id":id
    };
  }
}