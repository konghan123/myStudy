package hello.login.domain.login;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberReopsitory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberReopsitory memberReopsitory;

    public Member login(String loginId, String password) {
//        Optional<Member> findMemberOptional = memberReopsitory.findByLoginId(loginId);
//        Member member = findMemberOptional.get();
//        if(member.getPassword().equals(password)) {
//            return member;
//        } else {
//            return null;
//        }
        Optional<Member> byLoginId = memberReopsitory.findByLoginId(loginId);
        return byLoginId.filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }
}
