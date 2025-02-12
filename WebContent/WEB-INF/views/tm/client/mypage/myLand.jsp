<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/WEB-INF/views/include/header.jsp" />

<jsp:include page="/WEB-INF/views/include/top.jsp" />
<!-- //top end -->

	<jsp:include page="/WEB-INF/views/include/myMenu.jsp" />
	<!-- myMenu end -->
<div class="myPageWrap">
	<c:if test="${sessionScope.memId==null}">
		<script>
			alert("로그인 후에 접근 가능합니다");
			location.href='/filo/mem/loginForm.fl';
		</script>
	</c:if>
	
	<h2 class="pageTit" data-aos="fade-right" data-aos-duration="600">내가 작성한 랜드마크와 <br/>좋아요한 랜드마크를 확인해보세요.</h2>
	<!-- 지도 -->
	<div id="map"></div>

	<script type="text/javascript"
		src="//dapi.kakao.com/v2/maps/sdk.js?appkey=dbb3c6ebdae00379cc812a1240d45848&libraries=services,clusterer,drawing"></script>
	
	<script>
		function removeCheck(){
			if(confirm("정말 진심으로 삭제하시겠습니까?")==true){
				document.removefrm.submit();
			}else{
				return false;
			}
		}
		function cancelCheck(){
			if(confirm("정말 진심으로 취소하시겠습니까?")==true){
				document.cancelfrm.submit();
			}else{
				return false;
			}
		}
	</script>
	
	<script>
		$(document).ready(function(){
			
		//	다중 삭제
			$("input:checkbox[name=delLnum]").on("click",function(){
				var arr = new Array();
				$("input:checkbox[name=delLnum]:checked").each(function(){
					var checkVal = $(this).val();
					arr.push(checkVal);
				});
				$("#delLNo").val(arr);
			});
		
		//	다중 좋아요 취소
			$("input:checkbox[name=cnlLnum]").on("click",function(){
				var arr = new Array();
				$("input:checkbox[name=cnlLnum]:checked").each(function(){
					var checkVal = $(this).val();
					arr.push(checkVal);
				});
				$("#cnlLNo").val(arr);
			});
		
		//	*** 좋아요한 랜드마크 ***
			var positions = [];
			$.ajax({
				type: "POST",
				url: "/filo/land/myLikedLand.fl",
				dataType: "json",
				success : function(landList){ //요청 성공 콜백
					//받아온 리스트를 카카오 지도에 맞게 데이터 조합다시하고 
					for (var i = 0; i < landList.length; i ++) {
						positions.push(
							{title: landList[i].lName,
							latlng: new kakao.maps.LatLng(landList[i].xLoc,landList[i].yLoc)}
						);
					}
					likedMarkers();	//지도 뿌려라!
				}
			});
			
			// *** 지도 생성 *** 
			var mapContainer = document.getElementById('map'), // 지도를 표시할 div  
				mapOption = { 
					center: new kakao.maps.LatLng(37.56683861779011, 126.97864772708567), // 지도의 중심좌표
					level: 11 // 지도의 확대 레벨
				};
			var map = new kakao.maps.Map(mapContainer, mapOption); // 지도를 생성합니다
			
			function likedMarkers() {				
				// *** 마커 생성 *** 
				// 마커 이미지
				var imageSrc = "../resources/images/tm/landLikedImg.png"; 
				
				for (var i=0; i<positions.length; i++) {
					// 마커 이미지의 이미지 크기
					var imageSize = new kakao.maps.Size(40, 40);
					// 마커 이미지 생성   
					var markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize);
					// 마커를 생성합니다
					var marker = new kakao.maps.Marker({
						map: map, // 마커를 표시할 지도
						position: positions[i].latlng, // 마커를 표시할 위치
						title: positions[i].title, // 마커의 타이틀, 마커에 마우스를 올리면 타이틀이 표시됨
						image: markerImage, // 마커 이미지 
						clickable: true
					});
					
					// 마커 클릭 시 정보를 뿌려주기 위함
					kakao.maps.event.addListener(marker, 'click', function(mouseEvent) {
						console.log(this);
						
						event.preventDefault();
						var lName = this.Fb;
						var data = { "lName": lName };
						$.ajax({
							url: "/filo/land/landmarkCont.fl",
							type: "POST",
							dataType: "json",
							contentType: "application/json",
							data: JSON.stringify(data),
							success: function(res){
								console.log(res);
								$('#lNo').val(res.lNo);
								$('.content1').html("<br/><p class=\"mapLeft\">"+res.lName+"</p><p class=\"mapRight\">"+res.writer+"님의 랜드마크</p>");
								$('.content2').html("<p>"+res.lType+"&nbsp;&nbsp;|&nbsp;&nbsp;"+res.addr+"</p></br></br>");
								$('.content3').html("<p>"+res.lCont+"</p>");
							}
						});
						
					});//addListener
				}//for
			}//markers
			
		//	*** 작성한 랜드마크 ***
			var positions2 = [];
			$.ajax({
				type: "POST",
				url: "/filo/land/myWrittenLand.fl",
				dataType: "json",
				success : function(landList){
					for (var i = 0; i < landList.length; i ++) {
						positions2.push(
							{title: landList[i].lName,
							latlng: new kakao.maps.LatLng(landList[i].xLoc,landList[i].yLoc)}
						);
					}
					WrittenMarkers();
				}
			});
			function WrittenMarkers() {
				// *** 마커 생성 ***
				// 마커 이미지
				var imageSrc = "../resources/images/tm/landWrittenImg.png"; 
				
				for (var i=0; i<positions2.length; i++) {
					// 마커 이미지의 이미지 크기
					var imageSize = new kakao.maps.Size(40, 40);
					// 마커 이미지 생성   
					var markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize);
					// 마커를 생성합니다
					var marker = new kakao.maps.Marker({
						map: map, // 마커를 표시할 지도
						position: positions2[i].latlng, // 마커를 표시할 위치
						title: positions2[i].title, // 마커의 타이틀, 마커에 마우스를 올리면 타이틀이 표시됨
						image: markerImage, // 마커 이미지 
						clickable: true
					});
					
					// 마커 클릭 시 정보를 뿌려주기 위함
					kakao.maps.event.addListener(marker, 'click', function(mouseEvent) {
						console.log(this);
						
						event.preventDefault();
						var lName = this.Fb;
						var data = { "lName": lName };
						$.ajax({
							url: "/filo/land/landmarkCont.fl",
							type: "POST",
							dataType: "json",
							contentType: "application/json",
							data: JSON.stringify(data),
							success: function(res){
								console.log(res);
								$('#lNo').val(res.lNo);
								$('.content1').html("<br/><p class=\"mapLeft\">"+res.lName+"</p><p class=\"mapRight\">"+res.writer+"님의 랜드마크</p></br></br>");
								$('.content2').html("<p>"+res.lType+"&nbsp;&nbsp;|&nbsp;&nbsp;"+res.addr+"</p></br></br>");
								$('.content3').html("<p>"+res.lCont+"</p>");
							}
						});
						
					});//addListener
				}//for
			}//markers
			
			
		});//ready
	</script>
	
	<div class="btnMyWrap">
		<button onclick="window.location='/filo/land/landWrite.fl'" class="btnY">랜드마크 생성</button>
	</div>
	<div class="myLandCont">
		<!-- 지도 아래  -->
		<!-- 마커 클릭 시 보일 정보 -->
		<div class="Cont">
			<div class="mapBox">
				<p class="HomeSubTit">해당 랜드마크 정보</p>
				<div class="content1"></div>
				<div class="content2"></div>
				<div class="content3"></div>
			</div>
			<c:if test="${fn:length(lLand)==0}">
				<p class="HomeSubTit">아직 좋아요한 랜드마크가 없습니다.</p>
			</c:if>
			<c:if test="${fn:length(lLand)>0}">
				<p class="HomeSubTit">좋아요 한 랜드마크 | ${fn:length(lLand)}개</p>
				<form action="/filo/land/likedLandCancel.fl" name="cancelfrm" method="post" onsubmit="return cancelCheck()">
					<input type="hidden" name="lNo" id="cnlLNo"/>
					<c:forEach var="land" items="${lLand}">
						<ul class="likeLand">
							<input type="checkbox" name="cnlLnum" value="${land.lNo}"/>
							<li class="landTit">${land.lName}</li>
							<li>${land.lType}</li>
							<li>${land.addr}</li>
							<li>작성자:${land.writer}</li>
						</ul>
					</c:forEach>
					<div class="btnMyWrap">
						<button type="submit" class="btnC">좋아요 취소</button>
					</div>
				</form>
			</c:if>
			
			<c:if test="${fn:length(wLand)==0}">
				<p class="HomeSubTit">아직 작성한 랜드마크가 없습니다.</p>	
			</c:if>
			<c:if test="${fn:length(wLand)>0}">
				<p class="HomeSubTit">작성한 랜드마크 | ${fn:length(wLand)}개</p>
				<form action="/filo/land/myLandDelete.fl" name="removefrm" method="post" onsubmit="return removeCheck()">
					<input type="hidden" name="lNo" id="delLNo"/>
					<c:forEach var="land" items="${wLand}">
						<ul class="likeLand">
							<input type="checkbox" name="delLnum" value="${land.lNo}"/>
							<li class="landTit">${land.lName}</li>
							<li>${land.lType}</li>
							<li>${land.addr}</li>
						</ul>
					</c:forEach>
					<div class="btnMyWrap">
						<button type="submit" class="btnC lastBtn">랜드마크 삭제</button>
					</div>
				</form>
			</c:if>
			
		</div>
		<!-- cont end -->
	</div>
	<!-- myLandCont end -->
</div>
<!-- //wrapAll end -->

<jsp:include page="/WEB-INF/views/include/footer.jsp" />
<!-- //footer end -->