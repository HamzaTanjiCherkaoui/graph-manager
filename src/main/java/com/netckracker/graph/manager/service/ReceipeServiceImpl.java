/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.netckracker.graph.manager.service;

import com.netckracker.graph.manager.convertor.Convertor;
import com.netckracker.graph.manager.model.Catalog;
import com.netckracker.graph.manager.model.Node;
import com.netckracker.graph.manager.model.NodeResources;
import com.netckracker.graph.manager.model.Receipe;
import com.netckracker.graph.manager.model.ReceipeVersion;
import com.netckracker.graph.manager.model.Resources;
import com.netckracker.graph.manager.modelDto.ReceipeDto;
import com.netckracker.graph.manager.modelDto.ReceipeInformationDto;
import com.netckracker.graph.manager.repository.CatalogRepository;
import com.netckracker.graph.manager.repository.NodeRepository;
import com.netckracker.graph.manager.repository.NodeResourcesRepository;
import com.netckracker.graph.manager.repository.ReceipeRepository;
import com.netckracker.graph.manager.repository.ReceipeVersionRepository;
import com.netckracker.graph.manager.repository.ResourcesRepository;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 *
 * @author eliza
 */
@Service
public class ReceipeServiceImpl implements ReceipeService{
    @Autowired
    private CatalogRepository catalogRepository;
    @Autowired
    private ReceipeVersionRepository versionRepository;
    @Autowired
    private ReceipeRepository receipeRepository;
    @Autowired
    private NodeResourcesRepository nodeResourcesRepository;
    @Autowired
    private ResourcesRepository resourcesRepository;
    @Autowired
    private NodeService nodeService;
    @Autowired
    private Convertor convertor;
    @Autowired
    private NodeRepository nodeRepository;

    @Override
    @Transactional
    public void deleteReceipe(String receipeId, String userId) {
        Receipe receipe=receipeRepository.findByReceipeId(receipeId);
        if (receipe!=null)
        {
            ReceipeVersion find=versionRepository.findByReceipeAndUserId(receipe, userId);
            if (find!=null)
            {
               if (find.isIsMainVersion()==true)
               {
                   receipe.setIsDeleted(true);
                   receipeRepository.save(receipe);
               }
               else 
               {
                   List<NodeResources> resources=nodeResourcesRepository.findByVersion(find);
                   for (int i=0;i<resources.size();i++)
                   {
                       nodeResourcesRepository.delete(resources.get(i));
                   }
                   List<Node> nodes=nodeRepository.findByVersion(find);
                   for (int i=0; i<nodes.size();i++)
                   {
                       nodeService.deleteNode(nodes.get(i).getNodeId());
                   }
                   versionRepository.delete(find);
               }
            }
        }                
    }

    @Override
    @Transactional
    public ReceipeDto createReceipe(String name, String description, String catalogId, 
            String userId, boolean isPublic) {
        Receipe receipe=new Receipe();
        receipe.setName(name);
        receipe.setDescription(description);
        receipe.setIsCompleted(false);
        receipe.setIsPublic(isPublic);
        receipe.setIsDeleted(false);
        Catalog find=catalogRepository.findByCatalogId(catalogId);
        if (find!=null)
        {
          receipe.setCatalog(find);  
        }        
        Receipe saved=receipeRepository.save(receipe);
        
        ReceipeVersion version=new ReceipeVersion();        
        version.setIsMainVersion(true);
        version.setIsParalell(false);        
        version.setUserId(userId);
        version.setReceipe(saved);
        versionRepository.save(version);
        return convertor.convertReceipeToDto(saved);
        
    }

    @Override
    @Transactional
    public String addReceipeResources(String receipeId,String userId, String resourceId, double resourceNumber) {
        
        Receipe receipe=receipeRepository.findByReceipeId(receipeId);
        if (receipe!=null)
        {
            ReceipeVersion version=versionRepository.findByReceipeAndUserId(receipe, userId);
            Resources resource=resourcesRepository.findByResourceId(resourceId);        
            if (version!=null&&resource!=null)
            {
                NodeResources find=nodeResourcesRepository.findByResourceAndVersion(resource, version);
                if (find==null)
                {
                    NodeResources nodeResource=new NodeResources();
                    nodeResource.setResource(resource);
                    nodeResource.setVersion(version);
                    nodeResource.setNumberOfResource(resourceNumber);  

                    NodeResources saved=nodeResourcesRepository.save(nodeResource);
                    return saved.getNodeResourceId();
                }
                else 
                {
                    find.setNumberOfResource(resourceNumber);
                    NodeResources savedFind=nodeResourcesRepository.save(find);
                    return savedFind.getNodeResourceId();
                }                
            }
            else return null;
        }
        else return null;        
    }

    @Override
    public List<ReceipeDto> getPublicCompletedReceipes(int page, int size) {
        
        List<Receipe> receipes=receipeRepository.findByIsPublicAndIsCompleted(true, true,new PageRequest(page, size)).getContent();
            return receipes.stream()
               .map(receipe->convertor.convertReceipeToDto(receipe))
               .collect(Collectors.toList());
    }

    @Override
    public ReceipeInformationDto getReceipeInformation(String receipeId) {
        Receipe receipe=receipeRepository.findByReceipeId(receipeId);
        if (receipe!=null)
        {
            return convertor.convertReceipeToReceipeInformationDto(receipe);
        }
        else return null;        
    }
    
    @Override
    @Transactional
    public void createReceipeVersion(String receipeId, String userId)
    {
        Receipe receipe=receipeRepository.findByReceipeId(receipeId);
        if (receipe!=null)
        {
            ReceipeVersion version =versionRepository.findByReceipeAndUserId(receipe, userId);
            if (version==null)
            {
                ReceipeVersion mainVersion=versionRepository.findByReceipeAndIsMainVersion(receipe, true);           
                ReceipeVersion newVersion=new ReceipeVersion();
                newVersion.setIsMainVersion(false);
                newVersion.setUserId(userId);
                newVersion.setReceipe(receipe);
                ReceipeVersion savedVersion=versionRepository.save(newVersion);
                nodeService.copyReceipeVersion(mainVersion, savedVersion);
            }
        }             
    }

    @Override
    public List<ReceipeDto> getReceipesByCatalog(String catalogId, int page, int size) {
        Catalog catalog=catalogRepository.findByCatalogId(catalogId);
        if (catalog!=null)
        {
            List<Receipe> receipes=receipeRepository.findByIsPublicAndIsCompletedAndCatalog(true, true, catalog,new PageRequest(page, size)).getContent();
            return receipes.stream()
               .map(receipe->convertor.convertReceipeToDto(receipe))
               .collect(Collectors.toList());
        }
        else return null;        
    }

    @Transactional
    @Override
    public void setCompleted(String receipeId) {
        Receipe receipe=receipeRepository.findByReceipeId(receipeId);
        if (receipe!=null)
        {
            receipe.setIsCompleted(true);
            receipeRepository.save(receipe);
        }
    }      

    @Override
    public boolean isReceipeExcist(String receipeId) {
        Receipe receipe=receipeRepository.findByReceipeId(receipeId);
        if (receipe!=null)
        {
            return true;
        }
        else return false;
    }

    @Override
    public boolean isVersionCompleted(String receipeId) {
        Receipe receipe=receipeRepository.findByReceipeId(receipeId);
        if (receipe!=null)
        {
            return receipe.isIsCompleted();
        }
        return false;
    }
}
