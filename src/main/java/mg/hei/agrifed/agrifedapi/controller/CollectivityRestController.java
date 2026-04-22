package mg.hei.agrifed.agrifedapi.controller;

import mg.hei.agrifed.agrifedapi.dto.AssignCollectivityDto;
import mg.hei.agrifed.agrifedapi.dto.CollectivityDto;
import mg.hei.agrifed.agrifedapi.dto.CreateCollectivityDto;
import mg.hei.agrifed.agrifedapi.service.CollectivityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/collectivities")
public class CollectivityRestController {

    private final CollectivityService collectivityService;

    public CollectivityRestController(CollectivityService collectivityService) {
        this.collectivityService = collectivityService;
    }

    @PostMapping
    public ResponseEntity<List<CollectivityDto>> createCollectivities(
            @RequestBody List<CreateCollectivityDto> collectivities) {
        List<CollectivityDto> created = collectivityService.createCollectivities(collectivities);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<CollectivityDto> assignNameAndNumber(
            @PathVariable String id,
            @RequestBody AssignCollectivityDto dto) {
        CollectivityDto updated = collectivityService.assignNameAndNumber(id, dto);
        return ResponseEntity.ok(updated);
    }
}