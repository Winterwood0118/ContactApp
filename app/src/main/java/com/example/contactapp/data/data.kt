package com.example.contactapp.data

import com.example.contactapp.R

val contactList = mutableListOf(
    ContactInformation(
        name ="짐 레이너",
        email = "jim123@terran.com",
        phoneNumber = "010-1234-5678",
        relationship = "친구"
    ),
    ContactInformation(
        name ="타이커스 핀들레이",
        email = "tychus777@terran.com",
        phoneNumber = "010-4444-7777",
        relationship = "직원"
    ),
    ContactInformation(
        name ="아크튜러스 멩스크",
        email = "mengsk12@terran.com",
        phoneNumber = "010-1234-5678",
        relationship = "상사"
    ),
    ContactInformation(
        name ="아르타니스",
        email = "artanisss@protoss.com",
        phoneNumber = "010-2238-2238",
        relationship = "친구"
    ),
    ContactInformation(
        name ="제라툴",
        email = "zeratul@protoss.com",
        phoneNumber = "010-1234-5678",
        relationship = "선생님"
    ),
    ContactInformation(
        name ="보라준",
        email = "violetzun@protoss.com",
        phoneNumber = "010-5555-7777",
        relationship = "친구여친"
    ),
    ContactInformation(
        name ="사라 케리건",
        email = "kerriganluise@zerg.com",
        phoneNumber = "010-2473-2460",
        relationship = "친구"
    ),
    ContactInformation(
        name ="오버마인드",
        email = "overmind02@zerg.com",
        phoneNumber = "010-4444-4444",
        relationship = "웬수"
    ),
    ContactInformation(
        name ="알렉세이 스투코프",
        email = "alexeistukov@zerg.com",
        phoneNumber = "010-2448-0609",
        relationship = "운전기사"
    ),
    ContactInformation(
        name ="아바투르",
        email = "abathur0001@zerg.com",
        phoneNumber = "010-3333-2222",
        relationship = "원장님"
    )
)

val imageIdList = listOf(
    R.drawable.img_jim,
    R.drawable.img_tychus,
    R.drawable.img_mengsk,
    R.drawable.img_artanis,
    R.drawable.img_zeratul,
    R.drawable.img_vorazun,
    R.drawable.img_kerrigan,
    R.drawable.img_overmind,
    R.drawable.img_stukov,
    R.drawable.img_abathur
)

val myContact = ContactInformation(
    name = "홍길동",
    phoneNumber = "010-5536-8898",
    email = "eastwest@flash.com",
    relationship = "본인"
)