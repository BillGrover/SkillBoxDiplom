package root.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import root.dto.GlobalSettingsDto;
import root.handlers.GlobalHandler;
import root.model.GlobalSettings;
import root.repositories.GlobalSettingsRepository;

@Service
public class GlobalSettingsService {

    private final GlobalSettingsRepository globalSettingsRepo;
    private final GlobalHandler globalHandler;

    public GlobalSettingsService(GlobalSettingsRepository globalSettingsRepo, GlobalHandler globalHandler) {
        this.globalSettingsRepo = globalSettingsRepo;
        this.globalHandler = globalHandler;
    }

    public ResponseEntity<GlobalSettingsDto> getSettings() {
        return ResponseEntity.ok(
                new GlobalSettingsDto(
                        globalSettingsRepo.findByCode("MULTIUSER_MODE").getValue().equals("YES"),
                        globalSettingsRepo.findByCode("POST_PREMODERATION").getValue().equals("YES"),
                        globalSettingsRepo.findByCode("STATISTICS_IS_PUBLIC").getValue().equals("YES")));
    }

    public void putSettings(GlobalSettingsDto request) {
        if (globalHandler.getUserFromContext().getIsModerator() == 1){
            GlobalSettings gs;
            gs = globalSettingsRepo.findByCode("MULTIUSER_MODE");
            gs.setValue(request.isMultiuserMode() ? "YES" : "NO");
            globalSettingsRepo.save(gs);

            gs = globalSettingsRepo.findByCode("POST_PREMODERATION");
            gs.setValue(request.isPostPremoderation() ? "YES" : "NO");
            globalSettingsRepo.save(gs);

            gs = globalSettingsRepo.findByCode("STATISTICS_IS_PUBLIC");
            gs.setValue(request.isStatisticsIsPublic() ? "YES" : "NO");
            globalSettingsRepo.save(gs);
        }
    }
}
