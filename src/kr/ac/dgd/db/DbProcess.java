
package kr.ac.dgd.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kr.ac.dgd.core.Menu;
import kr.ac.dgd.input.InputUtil;

public class DbProcess {
    private static final String DB_URL  = "jdbc:mariadb://localhost:3306/DGD";
    private static final String DB_USER = "root";
    private static final String DB_PW   = "0000";

    private final String selectedNumber;

    // 생성자를 명시적으로 지정하면 기본생성자 DbProcess()는 호출될 수 없다. (따로 명시 해야 한다.)
    public DbProcess(String selectedNumber) {
        this.selectedNumber = selectedNumber;
    }

    public void startProcessing() throws SQLException {
        // Connection, PreparedStatement, ResultSet은 interface 객체이다.
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PW);
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        // selectedNumber에 따라 쿼리를 달리 한다.
        switch (this.selectedNumber){
            case Menu.SELECT:
                showAllStudents(conn, pstmt, rs); break;
            case Menu.INSERT: // 신규 Student를 추가 한다.
                // 이름은 뭔지 나이는 몇살인지 번호는 뭔지 이메일은 뭔지 입력을 받아서 쿼리 실행 하는 코드들...
                Student s = Student.buildStudent();
                // max id + 1을 가져온다, 새로운 id를 db에 insert 하기 위해
                pstmt = conn.prepareStatement("select max(id) + 1 from Student");
                rs = pstmt.executeQuery();
                if(rs.next()){
                    int maxId = rs.getInt(1);
                    System.out.println("maxId = " + maxId);
                    pstmt = conn.prepareStatement("insert into Student values (?, ?, ?, ?, ?)");
                    // "?"의 순서 (1부터)에 따라 값을 셋팅 한다.
                    pstmt.setInt(1, maxId);
                    pstmt.setString(2, s.getName());
                    pstmt.setInt(3, s.getAge());
                    pstmt.setString(4, s.getPhone());
                    pstmt.setString(5, s.getEmail());
                    System.out.println("INSERT 완료");

                    int updatedRows = pstmt.executeUpdate(); // db로 업데이트 (commit)
                    System.out.println("updatedRows: " + updatedRows);
                } else {
                    throw new SQLException("Can't execute query select max(id) + 1 from Student");
                }
                break;
            case Menu.UPDATE:
                // 학생 정보의 수정
                System.out.println("학생 정보의 수정");
                // 콘솔 출력: 전체 학생의 목록을 조회해서 사용자에게 보여주고
                List<Student> students = showAllStudents(conn, pstmt, rs);
                // 콘솔 출력: 수정할 학생의 번호(id)를 입력 하세요
                System.out.println("수정할 학생의 번호를 입력하세요.");
                int sIdToUpdate = InputUtil.getIdFromStudentList(students);
                System.out.println("수정할 학생 번호:" + sIdToUpdate);
                // 이름은 뭔지 나이는 몇살인지 번호는 뭔지 이메일은 뭔지 입력을 받는다
                Student updateStd = Student.buildStudent();
                // 업데이트 쿼리 수행
                pstmt = conn.prepareStatement("UPDATE Student SET name=?, age=?, phone=?, email=? where id=?");
                pstmt.setString(1, updateStd.getName());
                pstmt.setInt(2, updateStd.getAge());
                pstmt.setString(3, updateStd.getPhone());
                pstmt.setString(4, updateStd.getEmail());
                pstmt.setInt(5, sIdToUpdate);
                pstmt.executeUpdate();
                System.out.println("업데이트 완료.");
                break;
            case Menu.DELETE:
                // 삭제를 수행
                // 콘솔 출력 : 학생 정보의 삭제
                System.out.println("학생 정보의 삭제");
                // 전체 학생의 목록 출력
                List<Student> studentsList = showAllStudents(conn, pstmt, rs);
                // 콘솔 출력 : 삭제할 학생의 번호(id)를 입력 하세요
                System.out.println("삭제할 학생의 번호를 입력 하세요.");
                int sIdToDelete = InputUtil.getIdFromStudentList(studentsList);
                // 삭제 쿼리 수행
                pstmt = conn.prepareStatement("DELETE FROM Student where id=?");
                pstmt.setInt(1, sIdToDelete);
                pstmt.executeUpdate();
                System.out.println("학생 id " + sIdToDelete + "삭제 완료.");
                break;
            default :
                System.out.println("nothing to do"); break;
        }
    }

    private List<Student> showAllStudents(Connection conn, PreparedStatement pstmt, ResultSet rs) throws SQLException{
        List<Student> sList = new ArrayList<>();
        pstmt = conn.prepareStatement("select * from Student");
        rs = pstmt.executeQuery();
        /* 쿼리날린 결과를 가지고 콘솔에 출력한다. */
        while(rs.next()){
            System.out.println(
                    rs.getInt(1)    + " | "
                            + rs.getString(2) + " | "
                            + rs.getInt(3)    + " | "
                            + rs.getString(4) + " | "
                            + rs.getString(5));
            sList.add(new Student(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getString(5)));
        }
        return sList;
    }
}