package com.sazark.kykbecayis.offer;

import com.sazark.kykbecayis.misc.dto.OfferDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping
    public ResponseEntity<OfferDto> createOffer(@RequestBody OfferDto offerDto) {
        OfferDto savedOffer = offerService.create(offerDto);
        return ResponseEntity
                .created(URI.create("/api/offers/" + savedOffer.getId()))
                .body(savedOffer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfferDto> getOfferById(@PathVariable Long id) {
        OfferDto offerDto = offerService.findById(id);
        return (offerDto != null) ? ResponseEntity.ok(offerDto) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<OfferDto>> getAllOffers() {
        return ResponseEntity.ok(offerService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OfferDto> updateOffer(@PathVariable Long id, @RequestBody OfferDto offerDto) {
        OfferDto updatedOffer = offerService.update(id, offerDto);
        return (updatedOffer != null) ? ResponseEntity.ok(updatedOffer) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
        boolean deleted = offerService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
