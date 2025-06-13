package model.service.impl;

import model.repository.InsertProductRepository;
import model.service.InsertProductService;

public class InsertProductServiceImpl implements InsertProductService {
    private final InsertProductRepository repository;

    public InsertProductServiceImpl(InsertProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public long insert10Million() throws Exception {
        return repository.insert10Million();
    }

    @Override
    public long read10Million() throws Exception {
        return repository.read10Million();
    }
}
