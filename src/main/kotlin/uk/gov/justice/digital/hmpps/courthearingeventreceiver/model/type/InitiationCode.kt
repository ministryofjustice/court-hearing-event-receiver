package uk.gov.justice.digital.hmpps.courthearingeventreceiver.model.type

enum class InitiationCode(val description: String) {
  J("SJP Notice"),
  Q("Requisition"),
  S("Summons"),
  C("Charge"),
  R("Remitted"),
  O("Other"),
  Z("SJP Referral"),
}
