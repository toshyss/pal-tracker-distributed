package io.pivotal.pal.tracker.timesheets;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectClient {

    private final RestOperations restOperations;
    private final String endpoint;
    private Map<Long, ProjectInfo> projectsCache = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations = restOperations;
        this.endpoint = registrationServerEndpoint;
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
//        return restOperations.getForObject(endpoint + "/projects/" + projectId, ProjectInfo.class);
        ProjectInfo project = restOperations.getForObject(endpoint + "/projects/" + projectId, ProjectInfo.class);

        projectsCache.put(projectId, project);

        return project;
    }

    public ProjectInfo getProjectFromCache(long projectId) {
        logger.info("Getting project with id {} from cache", projectId);

        return projectsCache.get(projectId);
    }
}
