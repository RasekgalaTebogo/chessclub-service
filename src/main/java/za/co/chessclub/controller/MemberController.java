package za.co.chessclub.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.chessclub.model.*;
import za.co.chessclub.service.MemberService;

import java.util.List;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService service;

    @PostMapping
    public MemberDTO create( @Valid @RequestBody MemberCreateDTO member) {
        return service.addMember(member);
    }

    @GetMapping
    public List<MemberDTO> getAll() {
        return service.getAllMembers();
    }

    @GetMapping("/{id}")
    public MemberDTO get(@PathVariable Long id) {
        return service.getMember(id);
    }

    @PutMapping("/{id}")
    public MemberDTO update(@PathVariable Long id, @RequestBody MemberUpdateDTO member) {
        return service.updateMember(id, member);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteMember(id);
    }

    @PostMapping("/match")
    public void processMatch(@RequestBody MatchRequest request) {
        service.processMatch(request);
    }

    @GetMapping("/leaderboard")
    public List<MemberDTO> leaderboard() {
        return service.getAllMembers();
    }
}
