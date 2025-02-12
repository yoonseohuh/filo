<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/views/include/header.jsp" />
	<!-- 
	<link href="/resources/jqueryui/jquery-ui.theme.min.css" rel="stylesheet">
	<link href="/resources/jqueryui/jquery-ui.theme.css" rel="stylesheet">
	<script src="/resources/jqeryui/jquery-ui.js"></script>
	-->
	<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
	<jsp:include page="/WEB-INF/views/include/top.jsp" />
	<!-- //top end -->
	
	<div class="wrapAll client">
		<script>
			function validation(){
				var inputs = document.inputForm;
				if(!inputs.subject.value){
					alert("여행 제목을 입력해주세요");
					return false;
				}
				if(!inputs.concept.value){
					alert("여행 콘셉트를 정해주세요");
					return false;
				}
				if(!inputs.maxNum.value){
					alert("최대 인원수를 입력해주세요");
					return false;
				}
				if(!inputs.startDate.value){
					alert("여행 시작일을 정해주세요");
					return false;
				}
				if(!inputs.endDate.value){
					alert("여행 종료일을 정해주세요");
					return false;
				}
				if(!inputs.closingDate.value){
					alert("모집 마감일을 정해주세요");
					return false;
				}
				if(!inputs.loc.value){
					alert("여행 지역을 정해주세요");
					return false;
				}
				if(!inputs.courseExpl.value){
					alert("코스 설명을 작성해주세요");
					return false;
				}
				if(!inputs.cost.value){
					alert("예산을 입력해주세요");
					return false;
				}
				if(isNaN(inputs.cost.value)){
					alert("예산에 문자가 포함되어 있습니다");
					return false;
				}
			}
		
			$(document).ready(function(){
				//포지션 관련 스크립트
				$("input:checkbox[name=chk]").on("click",function(){
					//최대 3개 선택 가능하게 제한
					let count = $("input:checkbox[name=chk]:checked").length;
					if(count>3){
						$(this).prop("checked",false);
						alert("모집 포지션은 3개까지만 선택할 수 있습니다");
					}
					//3개 포지션 각각 po1, po2, po3 value에 넣기
					var arr = new Array();
					$("input:checkbox[name=chk]:checked").each(function(){
						var checkVal = $(this).val();
						arr.push(checkVal);
					});
					$("#po1").val(arr[0]);
					$("#po2").val(arr[1]);
					$("#po3").val(arr[2]);
					console.log("po1: "+$('#po1').val());
					console.log("po2: "+$('#po2').val());
					console.log("po3: "+$('#po3').val());
				});
				
				//날짜 관련 유효성 검사	
				//시작일
				$('#fromDate').datepicker({
					showOn: "both",
					//buttonImage: "images/calendar.gif",
					//buttonImageOnly: true,
					buttonText: "날짜선택",
					dateFormat: "yymmdd",
					changeMonth: true,
					minDate: 0,
					onClose: function(selectedDate) {    
						$("#toDate").datepicker("option","minDate",selectedDate);	//종료일의 최소일은 시작일
						$('#dueDate').datepicker("option","maxDate",selectedDate);	//마감일의 최대일은 시작일
					}                
				});
				//종료일
				$('#toDate').datepicker({
					showOn: "both",
					//buttonImage: "images/calendar.gif",
					//buttonImageOnly : true,
					buttonText: "날짜선택",
					dateFormat: "yymmdd",
					changeMonth: true,
					minDate: 0,
					onClose: function(selectedDate) {
						$("#fromDate").datepicker("option","maxDate",selectedDate);	//시작일의 최대일은 종료일
					}
				});
				//모집 마감일을 시작일 이전으로 설정하게 제한
				$('#dueDate').datepicker({
					showOn: "both",
					buttonText: "날짜선택",
					dateFormat: "yymmdd",
					minDate: 0,
					changeMonth: true,
				});
			});
		</script>
		
		<c:if test="${sessionScope.memId==null}">
			<script>
				alert("로그인 후에 작성 가능합니다");
				location.href='/filo/mem/loginForm.fl';
			</script>
		</c:if>
		
		<div class="makingContWrap">
			<div class="titWrap">
				<p class="tit2" data-aos="flip-down" data-aos-duration="600">여행 개설하기</p>
				<p class="txt" data-aos="flip-down" data-aos-duration="600">여행 사전계획을 작성해주세요!</p>
			</div>
			<form action="/filo/travel/makingWritePro.fl" name="inputForm" onsubmit="return validation()" method="post">
				<input type="hidden" name="po1" id="po1"/>
				<input type="hidden" name="po2" id="po2"/>
				<input type="hidden" name="po3" id="po3"/>
				<input type="hidden" name="id" value="${sessionScope.memId}"/>
				<div class="fl fl2">
					<div class="txtWrap">
						<p class="sub">여행 제목</p>
						<p class="txt txt2"><input type="text" name="subject" class="txt" placeholder="입력해주세요"/></p>
					</div>
					
					<div class="txtWrap">
						<p class="sub">지역</p>
						<p class="txt txt2">
							<select name="loc" class="txt">
								<optgroup label="수도권">
									<option value="seoul" selected>서울</option>
									<option value="incheon">인천</option>							
									<option value="gyunggi">경기</option>							
								</optgroup>
								<optgroup label="강원권">						
									<option value="gangwon">강원</option>
								</optgroup>
								<optgroup label="충청권">
									<option value="sejong">세종</option>
									<option value="daejeon">대전</option>
									<option value="chungnam">충청남도</option>
									<option value="chungbuk">충청북도</option>
								</optgroup>
								<optgroup label="전라권">
									<option value="gwangju">광주</option>
									<option value="jeonnam">전라남도</option>
									<option value="jeonbuk">전라북도</option>
								</optgroup>
								<optgroup label="경상권">
									<option value="busan">부산</option>
									<option value="ulsan">울산</option>
									<option value="daegu">대구</option>
									<option value="gyungnam">경상남도</option>
									<option value="gyungbuk">경상북도</option>
								</optgroup>
								<optgroup label="제주권">						
									<option value="jeju">제주</option>
								</optgroup>
							</select>
						</p>
					</div>
				</div>
				
				<div class="fl mz">
					<div class="txtWrap">
						<p class="sub">최대 인원</p>
						<p class="txt txt2"><input type="number" min="1" name="maxNum" class="txt" placeholder="입력해주세요"/></p>
					</div>
					
					<div class="txtWrap">
						<p class="sub">동성만</p>
						<p class="txt">
							<input type="checkbox" name="dongsung" value="1"/>
						</p>
					</div>
				</div>
				
				<p class="line"></p>
				
				<div class="txtWrap">
					<p class="sub">여행 콘셉트</p>
					<p class="txt">
						<input type="radio" name="concept" value="gourmet"/>맛집 &nbsp;
						<input type="radio" name="concept" value="healing"/>힐링 &nbsp;
						<input type="radio" name="concept" value="adventure"/>모험 &nbsp;
						<input type="radio" name="concept" value="tour"/>관광 &nbsp;
						<input type="radio" name="concept" value="camping"/>캠핑 &nbsp;
					</p>
				</div>
				
				<div class="txtWrap">
					<p class="sub">선호 가이드</p>
					<p class="txt">
						<c:forEach var="pos" items="${posList}">
							<input type="checkbox" name="chk" value="${pos.posNo}"/>${pos.posName} &nbsp;
						</c:forEach>
					</p>
				</div>
				
				<div class="txtWrap">
					<p class="sub">여행 기간</p>
					<p class="txt">
						시작일
						<input type="text" id="fromDate" name="startDate" class="txt2"/>
						~ 종료일
						<input type="text" id="toDate" name="endDate" class="txt2"/>
					</p>
				</div>
				
				<div class="txtWrap">
					<p class="sub">모집 마감</p>
					<p class="txt">
						마감일
						<input type="text" id="dueDate" name="closingDate" class="txt2"/>
					</p>
				</div>
				
				<div class="txtWrap">
					<p class="sub">코스 설명</p>
					<p class="txt txt3"><textarea name="courseExpl" rows="5" placeholder="코스 설명을 작성해주세요" style="resize:none;"></textarea></p>
				</div>
				
				<div class="txtWrap">
					<p class="sub">부가 설명</p>
					<p class="txt txt3"><textarea name="addExpl" rows="5" placeholder="부가 설명을 작성해주세요(교통편, 숙박형태 등)" style="resize:none;"></textarea></p>
				</div>
				
				<div class="txtWrap">
					<p class="sub">예산</p>
					<p class="txt txt2" style="padding: 0 15px;">약<input type="text" id="inputPrice" name="cost" placeholder="금액 입력" class="txt txt3"/>원</p>
				</div>
				
				
				<%-- <table border="1">
					<tr>
						<td>여행 제목</td>
						<td>
							<input type="text" name="subject"/>
						</td>
					</tr>
					<tr>
						<td>여행 콘셉트</td>
						<td>
							<input type="radio" name="concept" value="gourmet"/>맛집 &nbsp;
							<input type="radio" name="concept" value="healing"/>힐링 &nbsp;
							<input type="radio" name="concept" value="adventure"/>모험 &nbsp;
							<input type="radio" name="concept" value="tour"/>관광 &nbsp;
							<input type="radio" name="concept" value="camping"/>캠핑 &nbsp;
						</td>
					</tr>
					<tr>
						<td>가이드</td>
						<td>
							<c:forEach var="pos" items="${posList}">
								<input type="checkbox" name="chk" value="${pos.posNo}"/>${pos.posName} &nbsp;
							</c:forEach>
						</td>
					</tr>
					<tr>
						<td>최대 인원</td>
						<td><input type="number" min="1" name="maxNum"/>명</td>
					</tr>
					<tr>
						<td>여행 기간</td>
						<td>
							시작일
							<input type="text" id="fromDate" name="startDate"/>
							종료일
							<input type="text" id="toDate" name="endDate"/>
						</td>
					</tr>
					<tr>
						<td>모집 마감일</td>
						<td>
							<input type="text" id="dueDate" name="closingDate"/>
							* 모집 마감은 여행 시작일 이전으로 설정해주세요.
						</td>
					</tr>
					<tr>
						<td colspan="2">
							동성only <input type="checkbox" name="dongsung" value="1"/>
						</td>
					</tr>
					<tr>
						<td>지역</td>
						<td>
							<select name="loc">
								<optgroup label="수도권">
									<option value="seoul" selected>서울</option>
									<option value="incheon">인천</option>							
									<option value="gyunggi">경기</option>							
								</optgroup>
								<optgroup label="강원권">						
									<option value="gangwon">강원</option>
								</optgroup>
								<optgroup label="충청권">
									<option value="sejong">세종</option>
									<option value="daejeon">대전</option>
									<option value="chungnam">충청남도</option>
									<option value="chungbuk">충청북도</option>
								</optgroup>
								<optgroup label="전라권">
									<option value="gwangju">광주</option>
									<option value="jeonnam">전라남도</option>
									<option value="jeonbuk">전라북도</option>
								</optgroup>
								<optgroup label="경상권">
									<option value="busan">부산</option>
									<option value="ulsan">울산</option>
									<option value="daegu">대구</option>
									<option value="gyungnam">경상남도</option>
									<option value="gyungbuk">경상북도</option>
								</optgroup>
								<optgroup label="제주권">						
									<option value="jeju">제주</option>
								</optgroup>
							</select>
						</td>
					</tr>
					<tr>
						<td>코스설명</td>
						<td>
							<textarea name="courseExpl" rows="5" cols="90" placeholder="코스 설명을 작성해주세요" style="resize:none;"></textarea>
						</td>
					</tr>
					<tr>
						<td>부가설명</td>
						<td>
							<textarea name="addExpl" rows="5" cols="90" placeholder="부가 설명을 작성해주세요(교통편, 숙박형태 등)" style="resize:none;"></textarea>
						</td>
					</tr>
					<tr>
						<td>예산</td>
						<td>약 &nbsp; &#8361; <input type="text" id="inputPrice" name="cost"/></td>
					</tr>
					<tr>
						<td colspan="2" align="center">
							<input type="submit" value="개설"/>
							<input type="button" value="취소" onclick="window.location='/travelMaker/travel/makingList.tm'"/>
						</td>
					</tr>
				</table> --%>
				
				<div class="btnWrap">
					<input type="submit" value="개설" class="btn btnY"/>
					<a href="/filo/travel/makingList.fl"><p class="btn btnC">취소</p></a>
				</div>
			</form>
		</div>
		<!-- makingContWrap end -->
	</div>
	<!-- //wrapAll end -->
	
<jsp:include page="/WEB-INF/views/include/footer.jsp" />
<!-- //footer end -->

