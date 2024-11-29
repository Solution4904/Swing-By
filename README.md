# 개요

이름 : Swing By<br>
설명 : 현재 사용자의 위치 정보를 기반으로 등록해두었던 메모의 장소가 일정 거리 안에 존재할 때 이를 알려주는 메모 앱

# 필요 기술

- GPS (카카오맵 or 네이버맵 or 구글맵)
- 서버 (파이어베이스) or ROOM?
- 푸시 알림 (FCM) + Notification
- 위젯
- 백그라운드 서비스
- (추가 기능) SNS 계정 연동 (카카오톡, 네이버, 구글)

# 수정 필요

# 작업 이후 해야하는 것

- 전체 코드 리뷰
- class에 <>는 어떻게 쓰이는 거고, ::class.java나 ::inflater는 어떻게 쓰이는 거지?
- 메모 작성 레이아웃에 도움말을 '?' 아이콘을 만들어서 눌렀을 때만 툴팁으로 나오도록 ?
- 특정 동작이 완료되었을 때 함수 내에 해당 내용을 적는 방식이 아닌 플래그? 방식으로 특정 상황으로 바뀌었을 때 원하는 동작들을 따로 작성하는 방법은 없나?
- progress를 baseActivity단에서 create까지 끝마쳐놓고 사용하는 곳에서 show/hide만 호출할 수 있는 방법 없나? (binding.root.addview만 되면 해결될 것 같은데 baseActivity에선 addview가 호출되질 않음)
- FSM 가능? https://kimchanjung.github.io/design-pattern/2020/05/26/state-pattern/

# 고생했던 것들
- 프로그래스창을 나타내는 ProgressView class를 표시한 뒤 숨기고 나면 BottomSheetDialog의 터치가 비정상적이던 문제가 있었는데, 처음엔 터치를 제한하려고 사용한 setFlags가 문제인 줄 알았는데 다른 방식으로 제한해도 똑같아서 아예 프로그래스창을 나타낼 때 사용하려는 화면의 root view에 프로그래스창 화면을 addView해서 사용했으니 아예 romoveView를 하면 영향이 아예 사라지지 않을까 했는데 이 방법으로 해결됐음.