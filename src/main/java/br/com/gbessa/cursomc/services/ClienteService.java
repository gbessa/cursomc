package br.com.gbessa.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import br.com.gbessa.cursomc.domain.Cliente;
import br.com.gbessa.cursomc.dto.ClienteDTO;
import br.com.gbessa.cursomc.repositories.ClienteRepository;
import br.com.gbessa.cursomc.services.exceptions.DataIntegrityException;
import br.com.gbessa.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repo;

    public Cliente find(Integer id) {
	Optional<Cliente> obj = repo.findById(id);
	return obj.orElseThrow(() -> new ObjectNotFoundException(
		"Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
    }
    
    public List<Cliente> findAll() {
	return repo.findAll();
    }

    public Cliente insert(Cliente obj) {
	obj.setId(null); // Para garantir que o save vai inserir e não atualizar, caso algum id seja
			 // mandado
	return repo.save(obj);
    }

    public Cliente update(Cliente obj) {
	Cliente newObj = find(obj.getId());
	updateData(newObj, obj);
	return repo.save(newObj);
    }

    private void updateData(Cliente newObj, Cliente obj) {
	newObj.setNome(obj.getNome());
	newObj.setEmail(obj.getEmail());
    }

    public void delete(Integer id) {
	find(id);
	try {
	    repo.deleteById(id);	    
	}
	catch (DataIntegrityViolationException e) {
	    throw new DataIntegrityException("Não é possível excluir um cliente que possui pedidos");
	}
    }
    
    public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
	
	PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
	return repo.findAll(pageRequest);
	
    }
    
    public Cliente fromDTO(ClienteDTO objDto) {
	return new Cliente(objDto.getId(), objDto.getNome(), objDto.getEmail(), null, null);
    }
}
